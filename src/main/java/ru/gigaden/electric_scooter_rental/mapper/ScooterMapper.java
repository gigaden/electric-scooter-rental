package ru.gigaden.electric_scooter_rental.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.gigaden.electric_scooter_rental.dto.scooter.ScooterResponseDto;
import ru.gigaden.electric_scooter_rental.entity.Scooter;

@Mapper(componentModel = "spring")
public interface ScooterMapper {

    @Mapping(target = "rentalPointId", source = "rentalPoint.id")
    ScooterResponseDto mapScooterToResponseDto(Scooter scooter);
}
