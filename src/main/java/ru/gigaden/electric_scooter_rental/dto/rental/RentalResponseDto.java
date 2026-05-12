package ru.gigaden.electric_scooter_rental.dto.rental;

import ru.gigaden.electric_scooter_rental.entity.RentalStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Дто аренды для ответа клиенту.
 */
public record RentalResponseDto(UUID id,
                                UUID userId,
                                UUID scooterId,
                                RentalStatus status,
                                LocalDateTime startDate,
                                LocalDateTime endDate,
                                Integer startMileage,
                                Integer endMileage,
                                UUID tariffId,
                                UUID subscriptionId,
                                BigDecimal rentalCost) {
}