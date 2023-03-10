# FOODZILLA

To start application there are two options:

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

## To check api endpoints you can use Swagger documentation

[Local Swagger-UI](http://localhost:8080/swagger-ui/index.html#/)

### Reference Documentation

For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/3.0.2/maven-plugin/reference/html/)
* [Create an OCI image](https://docs.spring.io/spring-boot/docs/3.0.2/maven-plugin/reference/html/#build-image)

