from recommendation import mysql
from recommendation.recommendation import train_model, predict_with_model

from flask import Flask, jsonify

app = Flask(__name__)


@app.route('/train/<epochs>')
def hello_world(epochs):  # put application's code here
    mysql.update_data()
    return train_model(mysql.get_data(), int(epochs))


@app.route('/predict/<user>/<num_of_recommendations>')
def predict(user, num_of_recommendations):
    return jsonify(predict_with_model(user, int(num_of_recommendations), mysql.get_data()))


if __name__ == '__main__':
    app.run()
