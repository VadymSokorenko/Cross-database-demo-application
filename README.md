# Cross-database demo application

## How to run:

### In order to run application several preparation steps has to be executed:
* Install Docker Compose.
* Clone this repository.
* Run `mvn clean compile` command in terminal from project root to generate API interfaces and model classes. This phase will skip tests.
* Run `docker compose up -d` command in terminal from project root to start database containers.Alternatively use runner icon directly in `docker-compose.yaml`, if your IDE has docker compose support.
* Start application.

Use `mvn test` command to start test containers with databases and run tests against them.

### This project uses OpenAPI Specification 3.0

It states for design-first approach in application development.

Consider changing OpenAPI specification interface and model declaration before implementing controllers.

Controller classes must implement generated interfaces.
It's not restricted, but is considered a good practice and is a good point for future improvements.

API interfaces and model classes are generated from `openapi.yaml` in `resources` folder.



### Testcontainers support

This project
uses [Testcontainers at development time](https://docs.spring.io/spring-boot/docs/3.2.5/reference/html/features.html#features.testing.testcontainers.at-development-time).



Testcontainers has been configured to use the following Docker images:

* [`postgres:latest`](https://hub.docker.com/_/postgres)

