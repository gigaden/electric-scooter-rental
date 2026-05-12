package ru.gigaden.electric_scooter_rental.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.gigaden.electric_scooter_rental.dto.auth.AuthRequest;
import ru.gigaden.electric_scooter_rental.dto.auth.AuthResponse;
import ru.gigaden.electric_scooter_rental.service.AuthService;

/**
 * Контроллер авторизации
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Аутентификация", description = "Эндпоинты входа в систему")
public class AuthController {

  private final AuthService authService;

  /**
   * Авторизуем пользователя
   */
  @PostMapping("/login")
  @Operation(summary = "Вход в систему", description = "Получение JWT токена")
  public AuthResponse login(@Valid @RequestBody AuthRequest request) {

    log.info("Попытка входа пользователя {}", request.username());

    return authService.authenticate(request.username(), request.password());
  }
}