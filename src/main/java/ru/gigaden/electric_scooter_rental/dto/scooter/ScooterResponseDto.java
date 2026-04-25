package ru.gigaden.electric_scooter_rental.dto.scooter;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.gigaden.electric_scooter_rental.entity.ScooterStatus;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Дто самоката для ответа клиенту
 */
@Schema(description = "Дто самоката для ответа клиенту")
public record ScooterResponseDto(UUID id,
                                 UUID rentalPointId,
                                 Double latitude,
                                 Double longitude,
                                 String model,
                                 String description,
                                 ScooterStatus status,
                                 Integer batteryPower,
                                 Integer mileage,
                                 LocalDateTime updatedOn) {
}