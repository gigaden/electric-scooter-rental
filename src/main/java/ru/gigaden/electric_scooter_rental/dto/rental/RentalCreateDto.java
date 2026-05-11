package ru.gigaden.electric_scooter_rental.dto.rental;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.UUID;

/**
 * Дто для создания новой аренды.
 */
@Schema(description = "Дто для создания аренды")
public record RentalCreateDto(

    @NotNull(message = "id пользователя должен быть указан")
    @Schema(description = "id пользователя", example = "a1b2c3d4-...")
    UUID userId,

    @NotNull(message = "id самоката должен быть указан")
    @Schema(description = "id самоката", example = "e5f6g7h8-...")
    UUID scooterId,

    @NotNull(message = "Начальный пробег должен быть указан")
    @PositiveOrZero(message = "Начальный пробег должен быть >= 0")
    @Schema(description = "Пробег на момент начала аренды", example = "1250")
    Integer startMileage,

    @Schema(description = "id тарифа")
    UUID tariffId,

    @Schema(description = "id подписки пользователя")
    UUID userSubscriptionId
) {
}