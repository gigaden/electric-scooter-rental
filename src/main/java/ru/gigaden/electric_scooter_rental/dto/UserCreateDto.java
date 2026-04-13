package ru.gigaden.electric_scooter_rental.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

/**
 * Dto для создания пользователя
 */
@Builder
@Schema(description = "Дто для создания нового пользователя")
public record UserCreateDto(
        @NotNull(message = "Имя пользователя должно быть указано")
        @NotBlank(message = "Имя пользователя не должно быть пустыми")
        @Size(min = 3, max = 128, message = "Имя пользователя должно быть от 3 до 128 символов")
        @Schema(description = "Имя пользователя", example = "username")
        String username,

        @NotNull(message = "Пароль должен быть указано")
        @NotBlank(message = "Пароль не должен быть пустыми")
        @Size(min = 3, max = 128, message = "Пароль должен быть от 3 до 128 символов")
        @Schema(description = "Пароль пользователя", example = "password")
        String password,

        @NotNull(message = "Email должен быть указано")
        @NotBlank(message = "Email не должен быть пустыми")
        @Size(min = 3, max = 128, message = "Email должен быть от 3 до 128 символов")
        @Email(message = "Укажите верный формат электронной почты")
        @Schema(description = "Электронная почта", example = "mail@mail.ru")
        String email) {
}