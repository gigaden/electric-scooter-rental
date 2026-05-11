package ru.gigaden.electric_scooter_rental.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.gigaden.electric_scooter_rental.config.SecurityConfig;
import ru.gigaden.electric_scooter_rental.dto.user.UserCreateDto;
import ru.gigaden.electric_scooter_rental.dto.user.UserResponseDto;
import ru.gigaden.electric_scooter_rental.dto.user.UserUpdateDto;
import ru.gigaden.electric_scooter_rental.entity.UserSortField;
import ru.gigaden.electric_scooter_rental.exception.UserNotFoundException;
import ru.gigaden.electric_scooter_rental.exception.UserNotUniqueException;
import ru.gigaden.electric_scooter_rental.service.UserService;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
@DisplayName("Тесты контроллера пользователей")
class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private UserService userService;

  @Test
  @DisplayName("POST /users - успешное создание пользователя")
  void addUserShouldReturnOk() throws Exception {
    UserCreateDto requestDto = UserCreateDto.builder()
        .username("username")
        .password("password")
        .email("mail@mail.ru")
        .build();

    UUID userId = UUID.randomUUID();
    UserResponseDto responseDto = new UserResponseDto(
        userId,
        "username",
        "mail@mail.ru",
        null, null, Set.of()
    );

    when(userService.addUser(any(UserCreateDto.class))).thenReturn(responseDto);

    mockMvc.perform(post("/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestDto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("id").value(userId.toString()))
        .andExpect(jsonPath("username").value("username"))
        .andExpect(jsonPath("email").value("mail@mail.ru"));

    verify(userService, times(1)).addUser(any(UserCreateDto.class));
  }

  @Test
  @DisplayName("POST /users - ошибка 400 при невалидном теле запроса")
  void addUserInvalidRequestShouldReturnBadRequest() throws Exception {
    UserCreateDto invalidDto = UserCreateDto.builder()
        .username("username")
        .password("password")
        .email("")
        .build();

    mockMvc.perform(post("/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidDto)))
        .andExpect(status().isBadRequest());

    verify(userService, never()).addUser(any());
  }

  @Test
  @DisplayName("POST /users - конфликт при неуникальном email/username")
  void addUserDuplicateFieldShouldReturnConflict() throws Exception {
    UserCreateDto requestDto = UserCreateDto.builder()
        .username("username")
        .password("password")
        .email("mail@mail.ru")
        .build();

    when(userService.addUser(any(UserCreateDto.class)))
        .thenThrow(new UserNotUniqueException("Email не уникален"));

    mockMvc.perform(post("/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestDto)))
        .andExpect(status().isNotFound());

    verify(userService, times(1)).addUser(any());
  }

  @Test
  @DisplayName("GET /users/{userId} - успешное получение пользователя")
  void getUserShouldReturnOk() throws Exception {
    UUID userId = UUID.randomUUID();
    UserResponseDto responseDto = new UserResponseDto(
        userId,
        "username",
        "mail@mail.ru",
        null, null, Set.of()
    );

    when(userService.findUserById(userId)).thenReturn(responseDto);

    mockMvc.perform(get("/users/{userId}", userId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("id").value(userId.toString()))
        .andExpect(jsonPath("username").value("username"));

    verify(userService, times(1)).findUserById(userId);
  }

  @Test
  @DisplayName("GET /users/{userId} - пользователь не найден (404)")
  void getUserNotFoundShouldReturnNotFound() throws Exception {
    UUID userId = UUID.randomUUID();
    when(userService.findUserById(userId))
        .thenThrow(new UserNotFoundException("Пользователь не найден"));

    mockMvc.perform(get("/users/{userId}", userId))
        .andExpect(status().isNotFound());

    verify(userService, times(1)).findUserById(userId);
  }

  @Test
  @DisplayName("GET /users - получение списка пользователей")
  void findAllUsersShouldReturnOk() throws Exception {
    UserResponseDto dto = new UserResponseDto(
        UUID.randomUUID(),
        "username",
        "mail@mail.ru",
        null, null, Set.of()
    );

    when(userService.findAll(anyInt(), anyInt(), any()))
        .thenReturn(List.of(dto));

    mockMvc.perform(get("/users"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].username").value("username"));

    verify(userService).findAll(0, 10, UserSortField.USERNAME);
  }

  @Test
  @DisplayName("PUT /users/{id} - обновление пользователя")
  void updateUserShouldReturnOk() throws Exception {
    UUID id = UUID.randomUUID();

    UserUpdateDto request = new UserUpdateDto(
        "username",
        "password",
        "mail@mail.ru"
    );

    UserResponseDto response = new UserResponseDto(
        id,
        "username",
        "mail@mail.ru",
        null, null, Set.of()
    );

    when(userService.updateUserById(any(), any()))
        .thenReturn(response);

    mockMvc.perform(put("/users/{id}", id)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("id").value(id.toString()));

    verify(userService).updateUserById(any(), any());
  }

  @Test
  @DisplayName("DELETE /users/{id} - удаление пользователя")
  void deleteUserShouldReturnOk() throws Exception {
    UUID id = UUID.randomUUID();

    mockMvc.perform(delete("/users/{id}", id))
        .andExpect(status().isOk());

    verify(userService).deleteUserById(id);
  }
}