
FROM python:3.9.7-slim-bullseye
RUN mkdir -p recommendation-module
WORKDIR recommendation-module
COPY requirements.txt requirements.txt
RUN pip3 install -r requirements.txt

COPY . .

EXPOSE 5000

ENV FLASK_APP=recommendation/app.py

CMD [ "python3", "-m" , "flask", "run", "--host=0.0.0.0", "--port=5000"]
