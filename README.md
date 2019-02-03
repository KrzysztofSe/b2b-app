# b2b-app

## Running the application

### Prod

For prod configuration you will need to have docker installed. You can run the prod version in two ways:

##### Pull from Docker Hub

1. download the [docker-compose-standalone.yaml](https://github.com/KrzysztofSe/b2b-app/blob/master/docker-compose-standalone.yaml) to your host:  
<code>curl -O https://raw.githubusercontent.com/KrzysztofSe/b2b-app/master/docker-compose-standalone.yaml</code>
2. run <code>docker-compose -f docker-compose-standalone.yaml up</code>

##### Build from code

1. clone this repository <code>git clone https://github.com/KrzysztofSe/b2b-app.git</code>
2. run <code>mvn clean install</code>
3. run <code>docker-compose up</code>

Additionally you can scale the app instances by specifying <code>--scale app={count}</code> attribute.
    
### Local

By default the app is trying to connect to a mongodb instance on *localhost:27017* and is using *b2b* database. To run 
the app locally without any modifications in properties you will need to have a local mongodb instance up and running 
on default port *27017* with no authentication enabled - this is a standard mongo configuration after installation. 
If you want to use non-default db, possibly with authentication, you will need to configure mongo properties in 
[application-default.properties](https://github.com/KrzysztofSe/b2b-app/blob/master/src/main/resources/application-default.properties) file:
* <code>spring.data.mongodb.host</code> - the host where mongodb is running (default: *localhost*)
* <code>spring.data.mongodb.port</code> - the port the mongodb is listening on (default: *27017*)
* <code>spring.data.mongodb.username</code> - optional username, commented out when no authentication required (default: commented out)
* <code>spring.data.mongodb.password</code> - optional password, commented out when no authentication required (default: commented out)
* <code>spring.data.mongodb.database</code> - the db name to use (default: *b2b*)

##### Steps

1. clone this repository <code>git clone https://github.com/KrzysztofSe/b2b-app.git</code>
2. run <code>mvn clean spring-boot:run</code>

## Using the services

After launching, the app listens for requests on port 8080. In case of docker, the port 8080 is also exposed to the host. 

### API

1. <code>POST /basket</code> - creates new basket, returns basket id as String
2. <code>GET /basket/{id}</code> - retrieves the basket with contents, given the basket id
3. <code>DELETE /basket/{id}</code> - deletes the basket with given id
4. <code>POST /basket/{id}/order?vatNumber={vatNumber}</code> - places an order for basket with given id for a company 
with specified vatNumber
5. <code>PUT /basket/{id}/product</code> - modifies products in the basket with given id. Accepts the following payload:  
<code>{"id": "1", "quantity": 1}</code>  Behavior is as follows:
    * if the product with id "1" does not exist, it is added to the basket
    * if the product exists, the quantity is updated
    * if the given quantity is 0, the product is removed from the basket