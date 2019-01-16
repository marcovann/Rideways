# Submission

## Setup

```
- Requirements: maven, jdk 1.7+
- Tomcat credentials and port properties can be modified in pom.xml

```
Part 1 run tests:
```
mvn verify -PclientTest
```
Part 1 build-only jar:
```
mvn package -PclientRun
```
Part 2 run tests:
```
mvn verify -PserverTest
```
Part 2 run server:
```
mvn tomcat7:run-war -PserverRun
```


## Part 1

### Console application to print the search results for Dave's Taxis

`java -jar target/Rideways.jar dave 51.12,50.231 43.21,55.12`

### Console application to filter by number of passengers

`java -jar target/Rideways.jar all 51.12,50.231 43.21,55.12`

## Part 2
Request to a specific supplier:

`GET http://localhost:8080/Rideways/rest/{supplier_id}?pickup={pickup}&dropoff={dropoff}&num_passengers={num_passengers}`

`curl -X GET 'http://localhost:8080/Rideways/rest/dave?pickup=51.470020,-0.454295&dropoff=51.00000,1.0000'`

Request to all suppliers for the best rides:

`GET http://localhost:8080/Rideways/rest?pickup={pickup}&dropoff={dropoff}&num_passengers={num_passengers}`

`curl -X GET 'http://localhost:8080/Rideways/rest?pickup=51.470020,-0.454295&dropoff=51.00000,1.0000'`
