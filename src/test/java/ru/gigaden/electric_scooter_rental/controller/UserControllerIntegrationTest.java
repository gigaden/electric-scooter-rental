package ru.gigaden.electric_scooter_rental.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.gigaden.electric_scooter_rental.BaseIntegrationTest;
import ru.gigaden.electric_scooter_rental.dto.user.UserCreateDto;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class UserControllerIntegrationTest extends BaseIntegrationTest {

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  @DisplayName("POST /users - успешная регистрация нового пользователя")
  void addUserShouldReturnCreatedUser() throws Exception {
    UserCreateDto newUser = UserCreateDto.builder()
        .username("newuser")
        .password("securePass123")
        .email("newuser@example.com")
        .build();

    ResponseEntity<String> response = restTemplate.postForEntity(
        baseUrl() + "/users", newUser, String.class);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertNotNull(response.getBody());

    JsonNode json = objectMapper.readTree(response.getBody());
    assertNotNull(json.get("id"));
    assertEquals("newuser", json.get("username").asText());
    assertEquals("newuser@example.com", json.get("email").asText());
  }

  @Test
  @DisplayName("POST /users - ошибка 400 при невалидном email")
  void addUserWithInvalidEmailShouldReturnBadRequest() {
    UserCreateDto invalidUser = UserCreateDto.builder()
        .username("baduser")
        .password("pass")
        .email("invalid-email")
        .build();

    ResponseEntity<Map> response = restTemplate.postForEntity(
        baseUrl() + "/users", invalidUser, Map.class);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertThat(response.getBody().get("reason"))
        .isEqualTo("Неверный формат запроса");
  }

  @Test
  @DisplayName("POST /users - конфликт при дублирующемся email")
  void addUserWithDuplicateEmailShouldReturnConflict() {
    UserCreateDto duplicateUser = UserCreateDto.builder()
        .username("anotheruser")
        .password("pass123")
        .email("test@example.com")  // дубликат из test-data.sql
        .build();

    ResponseEntity<Map> response = restTemplate.postForEntity(
        baseUrl() + "/users", duplicateUser, Map.class);

    assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    assertThat(response.getBody().get("reason"))
        .isEqualTo("Конфликт уникальности данных");
  }

  @Test
  @DisplayName("GET /users/{id} - получение существующего пользователя")
  void getUserByIdShouldReturnUser() throws Exception {
    String userId = "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa";

    ResponseEntity<String> response = restTemplate.getForEntity(
        baseUrl() + "/users/" + userId, String.class);

    assertEquals(HttpStatus.OK, response.getStatusCode());

    JsonNode json = objectMapper.readTree(response.getBody());
    assertEquals(userId, json.get("id").asText());
    assertEquals("testuser", json.get("username").asText());
  }

  @Test
  @DisplayName("GET /users/{id} - пользователь не найден (404)")
  void getUserByIdNotFoundShouldReturnNotFound() {
    String nonExistentId = "00000000-0000-0000-0000-000000000000";

    ResponseEntity<Map> response = restTemplate.getForEntity(
        baseUrl() + "/users/" + nonExistentId, Map.class);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertThat(response.getBody().get("reason"))
        .isEqualTo("Ошибка при поиске пользователя");
  }
}