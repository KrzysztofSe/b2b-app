# b2b-app

## Running the application

### Prod

You can run the prod version in two ways:
1. Pull from Docker Hub
    * download the docker-compose-standalone.yaml to your host
    * run <code>docker-compose -f docker-compose-standalone.yaml up</code>
2. Build from code
    * clone this repository <code>git clone https://github.com/KrzysztofSe/b2b-app.git</code>
    * run <code>mvn clean install</code>
    * run <code>docker-compose up</code>
    
### Local

1. Clone this repository <code>git clone https://github.com/KrzysztofSe/b2b-app.git</code>
2. Run <code>mvn clean spring-boot:run</code>