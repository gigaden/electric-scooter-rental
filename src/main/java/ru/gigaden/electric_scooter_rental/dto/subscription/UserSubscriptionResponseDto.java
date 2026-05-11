package ru.gigaden.electric_scooter_rental.dto.subscription;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.gigaden.electric_scooter_rental.entity.SubscriptionStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Дто подписки для ответа клиенту")
public record UserSubscriptionResponseDto(
        UUID id,
        UUID userId,
        UUID tariffId,
        SubscriptionStatus status,
        LocalDateTime startDate,
        LocalDateTime endDate,
        LocalDateTime createdOn
) {
}