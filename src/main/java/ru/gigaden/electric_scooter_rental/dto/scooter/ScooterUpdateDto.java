package ru.gigaden.electric_scooter_rental.dto.scooter;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import ru.gigaden.electric_scooter_rental.entity.ScooterStatus;

import java.util.UUID;

/**
 * Dto для создания самоката
 */
@Builder
@Schema(description = "Дто для обновления самоката")
public record ScooterUpdateDto(
        @NotNull(message = "Id точки аренды должен быть указан")
        UUID rentalPointId,

        @NotNull(message = "Широта должна быть указана")
        @Schema(description = "Широта", example = "53.45")
        Double latitude,

        @NotNull(message = "Долгота должна быть указана")
        @Schema(description = "Долгота", example = "53.45")
        Double longitude,

        @NotNull(message = "Модель должна быть указана")
        @NotBlank(message = "Поле с моделью не должно быть пустыми")
        @Size(min = 2, max = 512, message = "Поле с моделью должно быть от 2 до 512 символов")
        @Schema(description = "Модель самоката", example = "Nimbus2000")
        String model,

        @NotNull(message = "Описание должно быть указано")
        @NotBlank(message = "Поле с описанием не должно быть пустыми")
        @Size(min = 16, max = 2048, message = "Поле с описанием должно быть от 16 до 2048 символов")
        @Schema(description = "Описание точки", example = "Описание точки аренды")
        String description,

        @NotNull(message = "Статус самоката должен быть указан")
        @Schema(description = "Статус самоката", example = "AVAILABLE")
        ScooterStatus status,

        @NotNull(message = "Заряд батареи должен быть указан")
        @Max(value = 100, message = "Значение заряда батареи не может быть больше 100")
        @Min(value = 0, message = "Заряд батареи не может быть меньше нуля")
        @Schema(description = "Заряд батареи самоката в процентах", example = "100")
        Integer batteryPower,

        @NotNull(message = "Пробег самоката должен быть указан")
        @Min(value = 0, message = "Пробег самоката не может быть меньше нуля")
        @Schema(description = "Пробег самоката", example = "130")
        Integer mileage) {
}
