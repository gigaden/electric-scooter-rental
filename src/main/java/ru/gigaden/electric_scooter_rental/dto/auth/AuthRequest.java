package ru.gigaden.electric_scooter_rental.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * Дто для аутентификации
 */
@Schema(description = "Запрос на аутентификацию")
public record AuthRequest(
    @NotBlank(message = "Имя пользователя обязательно")
    @Schema(description = "Имя пользователя", example = "username")
    String username,

    @NotBlank(message = "Пароль обязателен")
    @Schema(description = "Пароль", example = "password")
    String password
) {
}