package ru.gigaden.electric_scooter_rental.dto;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Дто для создания email сообщения пользователя.
 */
public record UserRegisteredEvent(
    UUID userId,
    String username,
    String email,
    LocalDateTime registeredAt
) {
}