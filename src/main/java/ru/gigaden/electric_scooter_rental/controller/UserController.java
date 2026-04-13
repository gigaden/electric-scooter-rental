package ru.gigaden.electric_scooter_rental.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.gigaden.electric_scooter_rental.dto.UserCreateDto;
import ru.gigaden.electric_scooter_rental.dto.UserResponseDto;
import ru.gigaden.electric_scooter_rental.service.UserService;

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
}
