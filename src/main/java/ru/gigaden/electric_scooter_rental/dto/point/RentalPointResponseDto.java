package ru.gigaden.electric_scooter_rental.dto.point;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.gigaden.electric_scooter_rental.dto.scooter.ScooterResponseDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Дто точки аренды для ответа клиенту
 */
@Schema(description = "Дто точки аренды для клиента")
public record RentalPointResponseDto(UUID id,
                                     String address,
                                     Double latitude,
                                     Double longitude,
                                     String description,
                                     LocalDateTime addedOn,
                                     LocalDateTime updatedOn,
                                     List<ScooterResponseDto> scooters) {
}
