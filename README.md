### About repository-search-service
The service fetches information about GitHub repositories nightly and stores it in a database for repository search purposes. 
It's developed using Oracle OpenJDK 21, Spring Boot, Gradle, PostgreSQL, and Docker, with integration testing performed using Testcontainers.

### Prerequisites
Ensure you have the following installed:

* Docker and Docker Compose for container management.
* Oracle OpenJDK 21 or compatible JDK for running and building the application.

### Running the app
To run the application in Docker run:
```
./gradlew bootJar && docker-compose up --build
```

To se the API docs and try the API, please navigate to Swagger UI
```
http://localhost:8080/webjars/swagger-ui/index.html
```