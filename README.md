# b2b-app

## Running the application

### Prod

For prod configuration you will need to have docker installed. You can run the prod version in two ways:

##### Pull from Docker Hub

1. download the [docker-compose-standalone.yaml](https://github.com/KrzysztofSe/b2b-app/blob/master/docker-compose-standalone.yaml) to your host
2. run <code>docker-compose -f docker-compose-standalone.yaml up</code>

##### Build from code

1. clone this repository <code>git clone https://github.com/KrzysztofSe/b2b-app.git</code>
2. run <code>mvn clean install</code>
3. run <code>docker-compose up</code>
    
### Local

By default the app is trying to connect to a mongodb instance on *localhost:27017* and is using *b2b* database. To run 
the app locally without any modifications in properties you will need to have a local mongodb instance up and running 
on port *27017* with no authentication enabled. If You want to use non-default db, possibly with authentication, you will 
need to configure mongo properties in [application-default.properties](https://github.com/KrzysztofSe/b2b-app/blob/master/src/main/resources/application-default.properties) file.

**Steps:**

1. Clone this repository <code>git clone https://github.com/KrzysztofSe/b2b-app.git</code>
2. Run <code>mvn clean spring-boot:run</code>