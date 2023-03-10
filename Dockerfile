FROM ringcentral/maven:3.8.2-jdk17

WORKDIR /foodzilla-backend
COPY /target/FOODZILLA.jar .

ENTRYPOINT ["java","-jar","FOODZILLA.jar"]