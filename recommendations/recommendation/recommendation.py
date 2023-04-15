from abc import ABC
import pandas as pd
import tensorflow as tf
import tensorflow_recommenders as tfrs


# Build a model.
class RankingModel(tf.keras.Model):

    def __init__(self, unique_user_ids, unique_product_ids):
        super().__init__()
        embedding_dimension = 32

        self.user_embeddings = tf.keras.Sequential([
            tf.keras.layers.experimental.preprocessing.StringLookup(
                vocabulary=unique_user_ids, mask_token=None),
            # add addional embedding to account for unknow tokens
            tf.keras.layers.Embedding(len(unique_user_ids) + 1, embedding_dimension)
        ])

        self.product_embeddings = tf.keras.Sequential([
            tf.keras.layers.experimental.preprocessing.StringLookup(
                vocabulary=unique_product_ids, mask_token=None),
            # add addional embedding to account for unknow tokens
            tf.keras.layers.Embedding(len(unique_product_ids) + 1, embedding_dimension)
        ])
        # Set up a retrieval task and evaluation metrics over the
        # entire dataset of candidates.
        self.ratings = tf.keras.Sequential([
            tf.keras.layers.Dense(256, activation="relu"),
            tf.keras.layers.Dense(64, activation="relu"),
            tf.keras.layers.Dense(1)
        ])

    def call(self, user_id, product_id):
        user_embeddings = self.user_embeddings(user_id)
        product_embeddings = self.product_embeddings(product_id)
        return self.ratings(tf.concat([user_embeddings, product_embeddings], axis=1))


# Build a model.
class FoodzillaModel(tfrs.models.Model, ABC):
    def __init__(self, preferences_data):
        super().__init__()
        self.ranking_model: tf.keras.Model = RankingModel(preferences_data['customer_id'].unique(),
                                                          preferences_data['recipe_id'].unique())
        self.task: tf.keras.layers.Layer = tfrs.tasks.Ranking(
            loss=tf.keras.losses.MeanSquaredError(),
            metrics=[tf.keras.metrics.RootMeanSquaredError()])

    def compute_loss(self, features, training=False):
        rating_predictions = self.ranking_model(features["userId"], features["productId"])

        return self.task(labels=features["rating"], predictions=rating_predictions)


def train_model(data):
    preferences_data = pd.DataFrame(data, columns=['customer_id', 'recipe_id', 'rating'])
    preferences_data['customer_id'] = preferences_data['customer_id'].apply(str)
    preferences_data['recipe_id'] = preferences_data['recipe_id'].apply(str)
    ratings = tf.data.Dataset.from_tensor_slices({"userId": tf.cast(preferences_data['customer_id'].values, tf.string),
                                                  "productId": tf.cast(preferences_data['recipe_id'].values, tf.string),
                                                  "rating": tf.cast(preferences_data['rating'].values, tf.int64, )})

    total_ratings = len(preferences_data.index)
    shuffled = ratings.shuffle(100_000, seed=42, reshuffle_each_iteration=False)
    train = shuffled.take(int(total_ratings * 0.8))
    test = shuffled.skip(int(total_ratings * 0.8)).take(int(total_ratings * 0.2))

    model = FoodzillaModel(preferences_data)
    model.compile(optimizer=tf.keras.optimizers.Adagrad(learning_rate=0.1))
    cached_train = train.shuffle(100_000).batch(8192).cache()
    cached_test = test.batch(4096).cache()
    model.fit(cached_train, epochs=10)

    user_rand = preferences_data['customer_id'][123]
    test_rating = {}
    for m in test.take(5):
        test_rating[m["productId"].numpy()] = model.ranking_model(tf.convert_to_tensor([user_rand]),
                                                                  tf.convert_to_tensor([m["productId"]]))
    print("Top 5 recommended products for User {}: ".format(user_rand))
    for m in sorted(test_rating, key=test_rating.get, reverse=True):
        print(m.decode())

    # print(model.evaluate(cached_test, return_dict=True))
