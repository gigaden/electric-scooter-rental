package ru.gigaden.electric_scooter_rental.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.gigaden.electric_scooter_rental.entity.Role;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

/**
 * Дто пользователя для ответа клиенту
 */
@Schema(description = "Дто с данными пользователя для клиента")
public record UserResponseDto(UUID id,
                              String username,
                              String email,
                              LocalDateTime registeredOn,
                              LocalDateTime updatedOn,
                              Set<Role> roles) {
}