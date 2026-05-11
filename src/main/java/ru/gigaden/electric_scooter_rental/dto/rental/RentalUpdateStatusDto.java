package ru.gigaden.electric_scooter_rental.dto.rental;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import ru.gigaden.electric_scooter_rental.entity.RentalStatus;

/**
 * Дто для изменения статуса аренды.
 */
public record RentalUpdateStatusDto(@NotNull(message = "Статус аренды должен быть указан")
                                    @Schema(description = "Статус аренды", example = "IN_PROGRESS")
                                    RentalStatus status) {
}