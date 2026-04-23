package ru.gigaden.electric_scooter_rental.mapper;

import org.mapstruct.Mapper;
import ru.gigaden.electric_scooter_rental.dto.point.RentalPointCreateDto;
import ru.gigaden.electric_scooter_rental.dto.point.RentalPointResponseDto;
import ru.gigaden.electric_scooter_rental.entity.RentalPoint;

@Mapper(componentModel = "spring")
public interface RentalPointMapper {

    RentalPoint mapCreateToRentalPoint(RentalPointCreateDto dto);

    RentalPointResponseDto mapRentalPointToResponse(RentalPoint rentalPoint);
}