package com.example.demo;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

import com.example.demo.config.ColumnMappingProperties;
import com.example.demo.config.DataSourcePropertiesConfiguration;
import com.example.demo.config.DatabaseOptions;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Testcontainers
public class UserControllerTest {

    @Autowired
    private DataSourcePropertiesConfiguration dataSourcePropertiesConfiguration;

    @LocalServerPort
    private String port;

    @Container()
    static PostgreSQLContainer<?> postgresDbOne = new PostgreSQLContainer<>("postgres")
            .withClasspathResourceMapping("db/db_1_init.sql", "/docker-entrypoint-initdb.d/db_1_init.sql", BindMode.READ_WRITE);

    @Container
    static PostgreSQLContainer<?> postgresDbTwo = new PostgreSQLContainer<>("postgres")
            .withClasspathResourceMapping("db/db_2_init.sql", "/docker-entrypoint-initdb.d/db_2_init.sql", BindMode.READ_WRITE);

    @BeforeEach
    void setUp() {
        ColumnMappingProperties firstColumnMappingProperties = new ColumnMappingProperties();
        firstColumnMappingProperties.setId("user_id");
        firstColumnMappingProperties.setUsername("login");
        firstColumnMappingProperties.setName("first_name");
        firstColumnMappingProperties.setSurname("last_name");

        DatabaseOptions firstDatabaseOptions = new DatabaseOptions();
        firstDatabaseOptions.setMapping(firstColumnMappingProperties);
        firstDatabaseOptions.setTable("users");
        firstDatabaseOptions.setUrl(postgresDbOne.getJdbcUrl());
        firstDatabaseOptions.setUser(postgresDbOne.getUsername());
        firstDatabaseOptions.setPassword(postgresDbOne.getPassword());

        ColumnMappingProperties secondColumnMappingProperties = new ColumnMappingProperties();
        secondColumnMappingProperties.setId("ldap_login");
        secondColumnMappingProperties.setUsername("ldap_login");
        secondColumnMappingProperties.setName("name");
        secondColumnMappingProperties.setSurname("surname");

        DatabaseOptions secondDatabaseOptions = new DatabaseOptions();
        secondDatabaseOptions.setMapping(secondColumnMappingProperties);
        secondDatabaseOptions.setTable("employees");
        secondDatabaseOptions.setUrl(postgresDbTwo.getJdbcUrl());
        secondDatabaseOptions.setUser(postgresDbTwo.getUsername());
        secondDatabaseOptions.setPassword(postgresDbTwo.getPassword());

        dataSourcePropertiesConfiguration.setDataSources(List.of(firstDatabaseOptions, secondDatabaseOptions));

        RestAssured.baseURI = "http://localhost:" + port;
    }

    @Test
    public void testApiCallToBothDatabases() {
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/users")
                .then()
                .statusCode(200)
                .body(".", hasSize(2));
    }

    @Test
    public void apiCallTestWithParamsForFirstDatabase() {
        given()
                .contentType(ContentType.JSON)
                .queryParam("username", "admin")
                .queryParam("name", "Vadym")
                .queryParam("surname", "Sokorenko")
                .when()
                .get("/users")
                .then()
                .assertThat()
                .body("username", equalTo(List.of("admin")))
                .body("name", equalTo(List.of("Vadym")))
                .body("surname", equalTo(List.of("Sokorenko")))
                .statusCode(200)
                .body(".", hasSize(1));
    }

    @Test
    public void apiCallTestWithParamsForSecondDatabase() {
        given()
                .contentType(ContentType.JSON)
                .queryParam("id", "user")
                .when()
                .get("/users")
                .then()
                .assertThat()
                .body("username", equalTo(List.of("user")))
                .body("name", equalTo(List.of("Test")))
                .body("surname", equalTo(List.of("Testenko")))
                .statusCode(200)
                .body(".", hasSize(1));
    }

    @Test
    public void testApiCallWithoutParams() {
        given()
                .contentType(ContentType.JSON)
                .queryParams("name", "MissingName")
                .when()
                .get("/users")
                .then()
                .statusCode(200)
                .body(".", hasSize(0));
    }
}
