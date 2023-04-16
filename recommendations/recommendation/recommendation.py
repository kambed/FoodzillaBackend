import os
from abc import ABC

import numpy as np
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


def train_model(data, epochs):
    preferences_data = pd.DataFrame(data, columns=['customer_id', 'recipe_id', 'rating'])
    preferences_data['customer_id'] = preferences_data['customer_id'].apply(str)
    preferences_data['recipe_id'] = preferences_data['recipe_id'].apply(str)
    ratings = tf.data.Dataset.from_tensor_slices({"userId": tf.cast(preferences_data['customer_id'].values, tf.string),
                                                  "productId": tf.cast(preferences_data['recipe_id'].values, tf.string),
                                                  "rating": tf.cast(preferences_data['rating'].values, tf.int64)})
    model = FoodzillaModel(preferences_data)
    model.compile(optimizer=tf.keras.optimizers.Adagrad(learning_rate=0.5))

    if os.path.exists("saved_model/"):
        model.load_weights("saved_model/")

    cached_train = ratings.shuffle(100_000).batch(8192).cache()
    model.fit(cached_train, epochs=epochs)

    model.save_weights("saved_model/")

    return model.evaluate(ratings.batch(4096).cache(), return_dict=True)


def predict_with_model(user_id, num_of_recommendations, data):
    preferences_data = pd.DataFrame(data, columns=['customer_id', 'recipe_id', 'rating'])
    preferences_data['customer_id'] = preferences_data['customer_id'].apply(str)
    preferences_data['recipe_id'] = preferences_data['recipe_id'].apply(str)
    model = FoodzillaModel(preferences_data)

    if os.path.exists("saved_model/"):
        model.load_weights("saved_model/").expect_partial()

    result_ratings = {}
    for r in np.random.choice(preferences_data['recipe_id'].unique(), size=5000, replace=False):
        result_ratings[r] = model.ranking_model(tf.convert_to_tensor([user_id]), tf.convert_to_tensor([r]))
    return sorted(result_ratings, key=result_ratings.get, reverse=True)[:num_of_recommendations]
