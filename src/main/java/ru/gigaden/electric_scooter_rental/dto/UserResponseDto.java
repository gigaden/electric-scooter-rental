package ru.gigaden.electric_scooter_rental.dto;

import ru.gigaden.electric_scooter_rental.entity.Role;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

/**
 * Дто пользователя для ответа клиенту
 * */
public record UserResponseDto(UUID id,
                              String username,
                              String email,
                              LocalDateTime registeredOn,
                              LocalDateTime updatedOn,
                              Set<Role> roles) {
}