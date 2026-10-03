package com.masprog.park_api;

import com.masprog.park_api.web.dto.UserCreateDto;
import com.masprog.park_api.web.dto.UserResponseDto;
import com.masprog.park_api.web.exception.ErrorMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.client.RestTestClient;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
@Sql(scripts = "/sql/users/user-insert.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/users/user-delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class UserIT {

    @Autowired
    RestTestClient testClient;

    @Test
    void createUser_WithValidUsernameAndPassword_ShouldReturnCreatedUserWithStatus201() {

        // Arrange
        UserCreateDto request =
                new UserCreateDto("tody@email.com", "123456");

        // Act
        UserResponseDto response = testClient
                .post()
                .uri("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(UserResponseDto.class)
                .returnResult()
                .getResponseBody();

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getId()).isNotNull();
        assertThat(response.getUsername()).isEqualTo("tody@email.com");
        assertThat(response.getRole()).isEqualTo("CLIENT");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "",
            "tody@",
            "tody@email"
    })
    void createUser_WithInvalidUsername_ShouldReturnStatus422(String username) {

        // Arrange
        UserCreateDto request =
                new UserCreateDto(username, "123456");

        // Act
        ErrorMessage response = testClient
                .post()
                .uri("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult()
                .getResponseBody();

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(422);

    }

    @ParameterizedTest
    @ValueSource(strings = {
            "",
            "12345",
            "1234567"
    })
    void createUser_WithInvalidPassword_ShouldReturnStatus422(String password) {

        // Arrange
        UserCreateDto request =
                new UserCreateDto("tody@gmail.com", password);

        // Act
        ErrorMessage response = testClient
                .post()
                .uri("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult()
                .getResponseBody();

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(422);
    }
}
