package ru.ochakovo.finpolicy;

import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import ru.ochakovo.finpolicy.annotation.IT;

@IT
@Testcontainers
@Sql("classpath:sql/data.sql")
public abstract class IntegrationTestBase {
    @Container
    @ServiceConnection
    static final PostgreSQLContainer<?> container = new PostgreSQLContainer<>(DockerImageName.parse("postgres:18.3"));
}
