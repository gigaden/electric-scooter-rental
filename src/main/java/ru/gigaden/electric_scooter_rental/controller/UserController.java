package ru.gigaden.electric_scooter_rental.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.gigaden.electric_scooter_rental.dto.UserCreateDto;
import ru.gigaden.electric_scooter_rental.dto.UserResponseDto;
import ru.gigaden.electric_scooter_rental.dto.UserUpdateDto;
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
    @Operation(summary = "Добавление пользователя", description = "Добавление нового пользователя в БД")
    public ResponseEntity<UserResponseDto> addUser(@Valid @RequestBody UserCreateDto dto) {
        log.info("Создаём нового пользователя {}", dto);
        UserResponseDto response = userService.addUser(dto);

        return ResponseEntity.ok(response);
    }

    /**
     * Получаем пользователя по его id
     */
    @GetMapping("/{userId}")
    @Operation(summary = "Получение пользователя", description = "Получение пользователя по его id")
    public ResponseEntity<UserResponseDto> getUser(@PathVariable UUID userId) {
        log.info("Получаем пользователя с id = {}", userId);
        UserResponseDto response = userService.findUserById(userId);

        return ResponseEntity.ok(response);
    }

    /**
     * Получаем всех пользователей
     */
    @GetMapping
    @Operation(summary = "Получение пользователей", description = "Получение пользователя с пагинацией и сортировкой")
    public ResponseEntity<Collection<UserResponseDto>> findAllUsers(@RequestParam(defaultValue = "0") int page,
                                                                    @RequestParam(defaultValue = "10") int size,
                                                                    @RequestParam(defaultValue = "USERNAME") String sort) {
        log.info("Получаем пользователей page={}, size={}, sort={}", page, size, sort);
        UserSortField sortField = UserSortField.fromString(sort);
        Collection<UserResponseDto> response = userService.findAll(page, size, sortField);

        return ResponseEntity.ok(response);
    }

    /**
     * Обновляем пользователя
     */
    @PutMapping("/{userId}")
    @Operation(summary = "Обновление пользователя", description = "Обновление пользователя по его id")
    public ResponseEntity<UserResponseDto> updateUserById(@PathVariable(name = "userId") UUID userId,
                                                          @Valid @RequestBody UserUpdateDto dto) {
        log.info("Обновляем пользователя с id {}", userId);
        UserResponseDto response = userService.updateUserById(userId, dto);

        return ResponseEntity.ok(response);
    }

    /**
     * Удаляем пользователя
     */
    @DeleteMapping("/{userId}")
    @Operation(summary = "Удаление пользователя", description = "Удаление пользователя по его id")
    public ResponseEntity<String> deleteUserById(@PathVariable(name = "userId") UUID userId) {
        log.info("Удаляем пользователя с id {}", userId);
        userService.deleteUserById(userId);

        return ResponseEntity.ok("Пользователь удалён");
    }

}
