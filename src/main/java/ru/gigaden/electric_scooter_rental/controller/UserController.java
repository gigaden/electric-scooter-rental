package ru.gigaden.electric_scooter_rental.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.gigaden.electric_scooter_rental.dto.user.UserCreateDto;
import ru.gigaden.electric_scooter_rental.dto.user.UserResponseDto;
import ru.gigaden.electric_scooter_rental.dto.user.UserUpdateDto;
import ru.gigaden.electric_scooter_rental.entity.UserSortField;
import ru.gigaden.electric_scooter_rental.service.UserService;

import java.util.Collection;
import java.util.UUID;

/**
 * Контроллер обрабатывает основные эндпоинты юзеров
 */
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Пользователи", description = "Контроллер для управления пользователями")
public class UserController {

  private final UserService userService;

  /**
   * Создаём нового пользователя
   */
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Добавление пользователя", description = "Добавление нового пользователя в БД")
  public UserResponseDto addUser(@Valid @RequestBody UserCreateDto dto) {

    log.info("Создаём нового пользователя {}", dto);

    return userService.addUser(dto);
  }

  /**
   * Получаем пользователя по его id
   */
  @GetMapping("/{userId}")
  @Operation(summary = "Получение пользователя", description = "Получение пользователя по его id")
  public UserResponseDto getUser(@PathVariable UUID userId) {

    log.debug("Получаем пользователя с id = {}", userId);

    return userService.findUserById(userId);
  }

  /**
   * Получаем всех пользователей
   */
  @GetMapping
  @PreAuthorize("hasRole('ADMIN')")
  @Operation(summary = "Получение пользователей", description = "(Админ) Получение пользователей с пагинацией и сортировкой")
  public Collection<UserResponseDto> findAllUsers(@RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "10") int size,
                                                  @RequestParam(defaultValue = "USERNAME") String sort) {
    log.debug("Получаем пользователей page={}, size={}, sort={}", page, size, sort);

    UserSortField sortField = UserSortField.fromString(sort);

    return userService.findAll(page, size, sortField);
  }

  /**
   * Обновляем пользователя
   */
  @PutMapping("/{userId}")
  @PreAuthorize("@securityUtil.isOwner(#userId) or hasRole('ADMIN')")
  @Operation(summary = "Обновление пользователя", description = "Обновление пользователя по его id")
  public UserResponseDto updateUserById(@PathVariable(name = "userId") UUID userId,
                                        @Valid @RequestBody UserUpdateDto dto) {

    log.info("Обновляем пользователя с id {}", userId);

    return userService.updateUserById(userId, dto);
  }

  /**
   * Удаляем пользователя
   */
  @DeleteMapping("/{userId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PreAuthorize("hasRole('ADMIN')")
  @Operation(summary = "Удаление пользователя", description = "Удаление пользователя по его id")
  public void deleteUserById(@PathVariable(name = "userId") UUID userId) {

    log.info("Удаляем пользователя с id {}", userId);

    userService.deleteUserById(userId);

  }

}
