package com.redcare.task;

import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

public class PostgresContainer {

    private static final String POSTGRES_IMAGE = "postgres:13.3";
    static volatile PostgreSQLContainer<?> instance;

    public static void startInstance() {
        if (instance == null) {
            synchronized (PostgresContainer.class) {
                if (instance == null) {
                    instance = startPostgresContainer();
                    int port = instance.getMappedPort(PostgreSQLContainer.POSTGRESQL_PORT);
                    String dbName = instance.getDatabaseName();
                    String username = instance.getUsername();
                    String password = instance.getPassword();

                    System.setProperty("spring.r2dbc.url", "r2dbc:postgresql://localhost:" + port + "/" + dbName);
                    System.setProperty("spring.r2dbc.username", username);
                    System.setProperty("spring.r2dbc.password", password);

                    System.setProperty("spring.flyway.url", "jdbc:postgresql://localhost:" + port + "/" + dbName);
                    System.setProperty("spring.flyway.user", username);
                    System.setProperty("spring.flyway.password", password);
                }
            }
        }
    }

    private static PostgreSQLContainer<?> startPostgresContainer() {
        PostgreSQLContainer<?> container = new PostgreSQLContainer<>(DockerImageName.parse(POSTGRES_IMAGE))
                .withDatabaseName("testDb");
        container.start();
        return container;
    }
}
