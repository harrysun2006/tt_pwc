## Description 
This is a simple api for an address book application with few basic functions:
- a user to store the name and phone numbers of their friends 
- display the list of friends and their corresponding phone numbers sorted by their name
- given two address books, display the list of friends that are unique to each address book

## Design Decisions

### Requirements
You have been asked to develop an address book that allows a user to store (between
successive runs of the program) the name and phone numbers of their friends, with the
following functionality:

- To be able to display the list of friends and their corresponding phone numbers sorted
by their name.
- Given another address book that may or may not contain the same friends, display the
list of friends that are unique to each address book (the union of all the relative
complements). For example given:
    - Book1 = { “Bob”, “Mary”, “Jane” }
    - Book2 = { “Mary”, “John”, “Jane” }
    - The friends that are unique to each address book are:
    - Book1 \ Book2 = { “Bob”, “John” }

### Assumption
- This is a single user system at this stage, i.e. no user or user relations are stored
- An address book can be empty, i.e. it doesn't contain any contact
- A contact's name is unique across system, i.e. the name identify the contact 
- No more than 100,000 contacts in one address book at this stage

### Scope
- The application will not implement full solution for authentication. i.e. Only authorisation be implemented. (Access to resources given identity rather than identity management.)
- The application will be a set of apis required to achieve - 
    - POST /user/ - create a user.
    - POST /addressbook/unique/ - check a user's friends against another list
    - GET /addressbook/ - get list of friends and contact numbers
    - POST /address/ - add friend names and contact numbers
    - GET /manage/user/ - management endpoint that gets all users (only use for verifying - since it isn't too straightforward to check data in dynamo local)

### De-scoped - due to the timing
- User management, security, ACL, full authentication implementation
- Containerisation: docker images and compose yaml 
- Live demo: a ReactJS based web UI
- CI/CD pipeline
- Micro-services architecture (AWS ECR/ECS)

### Strategy
- Use Alpine based postgreSQL docker container as a data store
- Use Groovy based Spock to run a data-driven test
- Apply a code coverage using jacoco with 90% limit
- Adopt jOOQ to build SQL queries and deal with database, as data survives longer than application
- Use flyway as database migration tool

## Application
This solution is built based on Spring Boot.

### Prerequisites
- Docker
- JDK 12

### Tech Stack
- JDK 12
- Spring Boot
- PostgreSQL
- jOOQ
- Flyway
- Mapstruct
- Spock
- JUnit
- Docker

## Build & Run
```bash
docker-compose up --build -d
./gradlew clean build
./gradlew bootrun
```
Then go to [Swagger Documentation](http://localhost:8081/swagger-ui.html#) for a simple API documentation

## Test
```bash
./gradlew test
```
Or import api/postman_test.json into Postman and test and run

### Finish
To stop and remove docker container
```bash
docker-compose down
```

## Reflection
- In MYSQL, catalog == schema, which cause some problems when running test in a separate test database with jOOQ 
- The flyway migration tool runs into some issues with embedded databases such as H2 Database, HSQLDB
- PostgreSQL in Alpine based docker container allows us to run test in a separate test database without contaminating the develop database
