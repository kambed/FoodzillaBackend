# FOODZILLA

## (OPTIONAL) If you want the API to generate AI aided recipes pictures follow:
Before starting backend you need to specify STABLE_DIFFUSION_API_URL in .env file.
The simplest way of getting such URL is to use Google Colab:

> https://colab.research.google.com/github/saharmor/dalle-playground/blob/main/backend/dalle_playground_backend.ipynb

There you start the code and copy Cloudflare URL (should look like your url is: https://xxxxxx.trycloudflare.com)

Another option is to run it locally, you have to install Python 3.9 and run it with commands (in stable-diffusion directory):
> pip install -r ./requirements.txt
> 
> pip3 install torch==1.10.1+cu113 torchvision==0.11.2+cu113 torchaudio===0.10.1+cu113 -f https://download.pytorch.org/whl/cu113/torch_stable.html
> 
> python app.py

Alternatively, for JetBrains PyCharm users they can use green button in app.py file.

Another option is to start docker container with Dockerfile (file is in stable-diffusion folder) 

> docker build -t stableDiffusion
> 
> docker run -d stableDiffusion

Alternatively, for JetBrains PyCharm users they can use green triangle button on the left in Dockerfile.

WARNING: THIS OPERATION REQUIRES MORE THAN 20GB FREE SPACE ON DISK!

## To start application there are two options:

## 1.) Database docker + Local Spring backend

To start database on docker use command:
> docker-compose -f docker-compose.yml up db

Alternatively, for JetBrains Intellij users they can use green button on the left in opened docker-compose file in db section.

After this you can build Spring backend locally in your environment:
> mvn clean install
> 
> mvn spring-boot:run

Alternatively, for JetBrains Intellij users click green button in opened FoodzillaBackendApplication file

## 2.) Docker Database and Spring backend
Before dockerizing you have to create .jar file with command:

> mvn package -DskipTests

To start application within docker containers you can use created docker-compose file

> docker-compose -f docker-compose.yml up

Docker-compose will automatically create database, populate it with test data and start spring backend afterwards.
No further action is required, Dockerfile is only used to build spring application as the part of the docker-compose.

Alternatively, for JetBrains Intellij users they can use green button on the left in opened docker-compose file.

## To check api endpoints you can use GraphiQL documentation

[GraphiQL UI](http://localhost:8080/graphiql?path=/graphql)

## Troubleshooting
If MySQL drops error when using search endpoint, execute following query on database:

> SET GLOBAL sql_mode=(SELECT REPLACE(@@sql_mode,'ONLY_FULL_GROUP_BY',''));

### Reference Documentation

For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/3.0.2/maven-plugin/reference/html/)
* [Create an OCI image](https://docs.spring.io/spring-boot/docs/3.0.2/maven-plugin/reference/html/#build-image)

