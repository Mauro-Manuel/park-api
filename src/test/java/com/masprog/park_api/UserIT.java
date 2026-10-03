package com.masprog.park_api;

import com.masprog.park_api.entity.User;
import com.masprog.park_api.repository.UserRepository;
import com.masprog.park_api.web.dto.UserCreateDto;
import com.masprog.park_api.web.dto.UserPasswordDto;
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
import org.springframework.core.ParameterizedTypeReference;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
@Sql(scripts = "/sql/users/user-insert.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/users/user-delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class UserIT {

    @Autowired
    RestTestClient testClient;

    @Autowired
    private UserRepository userRepository;

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

    @Test
    void createUser_WithExistingUsername_ShouldReturnStatus409() {

        // Arrange
        UserCreateDto request =
                new UserCreateDto("ana@email.com", "123456");

        // Act
        ErrorMessage response = testClient
                .post()
                .uri("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .exchange()
                .expectStatus().isEqualTo(409)
                .expectBody(ErrorMessage.class)
                .returnResult()
                .getResponseBody();

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(409);
        assertThat(response.getMessage())
                .isEqualTo("Username ana@email.com already exists");
    }


    @Test
    void findUserById_WithExistingId_ShouldReturnUserWithStatus200() {

        // Arrange
        Long userId = 100L;

        // Act
        UserResponseDto response = testClient
                .get()
                .uri("/api/v1/users/{id}", userId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserResponseDto.class)
                .returnResult()
                .getResponseBody();

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(userId);
        assertThat(response.getUsername()).isEqualTo("ana@email.com");
        assertThat(response.getRole()).isEqualTo("ADMIN");
    }

    @Test
    void findUserById_WithNonExistingId_ShouldReturnStatus404() {

        // Arrange
        Long userId = 0L;

         // Act
        ErrorMessage response = testClient
                .get()
                .uri("/api/v1/users/{id}", userId)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorMessage.class)
                .returnResult()
                .getResponseBody();

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(404);
        assertThat(response.getMessage())
                .isEqualTo("User id=0 not found");
    }

    @Test
    void changePassword_WithValidData_ShouldReturnStatus204(){
        // Arrange
        Long userId = 100L;
        UserPasswordDto request =
                new UserPasswordDto("123456", "101010", "101010");

        // Act
        testClient
                .patch()
                .uri("/api/v1/users/{id}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .exchange()
                .expectStatus().isNoContent()
                .expectBody().isEmpty();

        // Assert
        User user = userRepository.findById(userId).orElseThrow();

        assertThat(user.getPassword()).isEqualTo("101010");
    }

    @Test
    void changePassword_WithNonExistingUserId_ShouldReturnStatus404(){
        // Arrange
        Long userId = 0L;

        UserPasswordDto request =
                new UserPasswordDto("123456", "101010", "101010");

        // Act
        ErrorMessage response = testClient
                .patch()
                .uri("/api/v1/users/{id}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorMessage.class)
                .returnResult()
                .getResponseBody();

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(404);
        assertThat(response.getMessage())
                .isEqualTo("User id=0 not found");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "",
            "12345",
            "1234567"
    })
    void changePassword_WithInvalidData_ShouldReturnStatus422(String password) {

        // Arrange
        Long userId = 100L;

        UserPasswordDto request =
                new UserPasswordDto(password, password, password);

        // Act
        ErrorMessage response = testClient
                .patch()
                .uri("/api/v1/users/{id}", userId)
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

    @Test
    void changePassword_WithDifferentPasswordConfirmation_ShouldReturnStatus400() {

        // Arrange
        Long userId = 100L;

        UserPasswordDto request =
                new UserPasswordDto(
                        "123456",
                        "123456",
                        "000000"
                );

        // Act
        ErrorMessage response = testClient
                .patch()
                .uri("/api/v1/users/{id}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorMessage.class)
                .returnResult()
                .getResponseBody();

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(400);
        assertThat(response.getMessage())
                .isEqualTo(
                        "New password does not match the password confirmation."
                );
    }

    @Test
    void changePassword_WithIncorrectCurrentPassword_ShouldReturnStatus400() {

        // Arrange
        Long userId = 100L;

        UserPasswordDto request =
                new UserPasswordDto(
                        "000000",
                        "123456",
                        "123456"
                );

        // Act
        ErrorMessage response = testClient
                .patch()
                .uri("/api/v1/users/{id}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorMessage.class)
                .returnResult()
                .getResponseBody();

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(400);
        assertThat(response.getMessage())
                .isEqualTo(
                        "Your password does not match."
                );
    }

    @Test
    void getAllUsers_WithExistingUsers_ShouldReturnUsersWithStatus200() {

        // Act
        List<UserResponseDto> response = testClient
                .get()
                .uri("/api/v1/users")
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<UserResponseDto>>() {})
                .returnResult()
                .getResponseBody();

        // Assert
        assertThat(response).isNotNull();
        assertThat(response).hasSize(3);
        assertThat(response)
                .extracting(
                        UserResponseDto::getId,
                        UserResponseDto::getUsername,
                        UserResponseDto::getRole
                )
                .containsExactlyInAnyOrder(
                        tuple(100L, "ana@email.com", "ADMIN"),
                        tuple(101L, "bia@email.com", "CLIENT"),
                        tuple(102L, "bob@email.com", "CLIENT")
                );
    }

}
