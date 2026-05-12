package ru.gigaden.electric_scooter_rental.dto.role;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

/**
 * Dto для создания роли пользователя.
 */
@Builder
@Schema(description = "Дто для создания роли пользователя")
public record RoleCreateDto(@NotNull(message = "Роль пользователя должна быть указано")
                            @NotBlank(message = "Роль пользователя не должна быть пустыми")
                            @Size(min = 3, max = 128, message = "Роль пользователя должна быть от 3 до 128 символов")
                            @Schema(description = "Роль пользователя", example = "USER")
                            String name) {
}
