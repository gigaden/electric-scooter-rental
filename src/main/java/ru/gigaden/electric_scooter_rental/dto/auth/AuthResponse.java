package ru.gigaden.electric_scooter_rental.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(description = "Ответ с токеном доступа")
public record AuthResponse(
        @Schema(description = "JWT токен")
        String token,

        @Schema(description = "ID пользователя")
        UUID userId
) {
}