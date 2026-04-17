package ru.gigaden.electric_scooter_rental.dto.role;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

/**
 * Дто роли пользователя для ответа клиенту
 */
@Schema(description = "Дто с ролью пользователя для клиента")
public record RoleResponseDto(UUID id,
                              String name) {
}