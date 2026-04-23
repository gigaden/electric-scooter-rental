package ru.gigaden.electric_scooter_rental.mapper;

import org.mapstruct.Mapper;
import ru.gigaden.electric_scooter_rental.dto.scooter.ScooterResponseDto;
import ru.gigaden.electric_scooter_rental.entity.Scooter;

@Mapper(componentModel = "spring")
public interface ScooterMapper {

    ScooterResponseDto mapScooterToResponseDto(Scooter scooter);
}
