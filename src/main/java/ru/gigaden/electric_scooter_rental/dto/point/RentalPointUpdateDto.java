package ru.gigaden.electric_scooter_rental.dto.point;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Dto для обновления точки аренды.
 */
@Schema(description = "Дто для обновления точки аренды")
public record RentalPointUpdateDto(@NotNull(message = "Адрес должен быть указан")
                                   @NotBlank(message = "Поле с адресом не должно быть пустыми")
                                   @Size(min = 16, max = 1024, message = "Поле с адресом должно быть от 16 до 1024 символов")
                                   @Schema(description = "Адрес", example = "Н.Новгород, Бурнаковская 103")
                                   String address,

                                   @NotNull(message = "Широта должна быть указана")
                                   @Schema(description = "Широта", example = "53.45")
                                   Double latitude,

                                   @NotNull(message = "Долгота должна быть указана")
                                   @Schema(description = "Долгота", example = "53.45")
                                   Double longitude,

                                   @NotNull(message = "Описание должно быть указано")
                                   @NotBlank(message = "Поле с описанием не должно быть пустыми")
                                   @Size(min = 16, max = 2048, message = "Поле с описанием должно быть от 16 до 2048 символов")
                                   @Schema(description = "Описание точки", example = "Описание точки аренды")
                                   String description) {
}
