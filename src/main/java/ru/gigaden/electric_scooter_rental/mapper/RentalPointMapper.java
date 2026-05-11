package ru.gigaden.electric_scooter_rental.mapper;

import org.mapstruct.Mapper;
import ru.gigaden.electric_scooter_rental.dto.point.RentalPointCreateDto;
import ru.gigaden.electric_scooter_rental.dto.point.RentalPointResponseDto;
import ru.gigaden.electric_scooter_rental.entity.RentalPoint;

/**
 * Маппер для точек аренды.
 */
@Mapper(componentModel = "spring", uses = {ScooterMapper.class})
public interface RentalPointMapper {

  RentalPoint mapCreateToRentalPoint(RentalPointCreateDto dto);

  RentalPointResponseDto mapRentalPointToResponse(RentalPoint rentalPoint);
}