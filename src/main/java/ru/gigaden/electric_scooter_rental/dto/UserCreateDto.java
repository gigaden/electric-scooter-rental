package ru.gigaden.electric_scooter_rental.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Dto для создания пользователя
 */
public record UserCreateDto(
        @NotNull(message = "Имя пользователя должно быть указано")
        @NotBlank(message = "Имя пользователя не должно быть пустыми")
        @Size(min = 3, max = 128, message = "Имя пользователя должно быть от 3 до 128 символов")
        String username,

        @NotNull(message = "Пароль должен быть указано")
        @NotBlank(message = "Пароль не должен быть пустыми")
        @Size(min = 3, max = 128, message = "Пароль должен быть от 3 до 128 символов")
        String password,

        @NotNull(message = "Email должен быть указано")
        @NotBlank(message = "Email не должен быть пустыми")
        @Size(min = 3, max = 128, message = "Email должен быть от 3 до 128 символов")
        @Email(message = "Укажите верный формат электронной почты")
        String email) {
}