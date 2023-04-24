import mysql.connector
import os
from dotenv import load_dotenv

load_dotenv()


class MySQLDataProvider:

    def __init__(self):
        self.data = self.__get_data_db()

    def update_data(self):
        self.data = self.__get_data_db()

    def get_data(self):
        return self.data

    def __get_data_db(self):
        mydb = mysql.connector.connect(
            host=os.getenv('MYSQL_HOST'),
            port=os.getenv('MYSQL_LOCAL_PORT'),
            user=os.getenv('MYSQL_USER'),
            password=os.getenv('MYSQL_PASSWORD'),
            database=os.getenv('MYSQL_DATABASE')
        )

        cursor = mydb.cursor()
        cursor.execute("SELECT * FROM preference")
        return cursor.fetchall()
