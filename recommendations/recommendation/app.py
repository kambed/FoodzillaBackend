from recommendation.mysql import get_data_db
from recommendation.recommendation import train_model

from flask import Flask

app = Flask(__name__)


@app.route('/')
def hello_world():  # put application's code here
    train_model(get_data_db())
    return 'Hello World!'


if __name__ == '__main__':
    app.run()
