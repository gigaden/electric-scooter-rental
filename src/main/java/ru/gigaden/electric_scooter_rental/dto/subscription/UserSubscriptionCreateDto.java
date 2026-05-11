package ru.gigaden.electric_scooter_rental.dto.subscription;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Дто для создания подписки")
public record UserSubscriptionCreateDto(
        @NotNull(message = "ID пользователя должен быть указан")
        @Schema(description = "ID пользователя", example = "a1b2c3d4-...")
        UUID userId,

        @NotNull(message = "ID тарифа должен быть указан")
        @Schema(description = "ID подписочного тарифа", example = "e5f6g7h8-...")
        UUID tariffId,

        @NotNull(message = "Дата начала должна быть указана")
        @Schema(description = "Дата начала подписки", example = "2024-01-01 00:00:00")
        LocalDateTime startDate,

        @NotNull(message = "Дата окончания должна быть указана")
        @Schema(description = "Дата окончания подписки", example = "2024-12-31 23:59:59")
        LocalDateTime endDate
) {
}