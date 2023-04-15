import mysql.connector


def get_data_db():
    mydb = mysql.connector.connect(
        host="localhost",
        user="user",
        password="password",
        database="db"
    )

    cursor = mydb.cursor()
    cursor.execute("SELECT * FROM preference")
    return cursor.fetchall()
