package ru.gigaden.electric_scooter_rental.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.gigaden.electric_scooter_rental.dto.rental.RentalResponseDto;
import ru.gigaden.electric_scooter_rental.entity.Rental;

/**
 * Маппер для аренды
 * */
@Mapper(componentModel = "spring")
public interface RentalMapper {

    @Mapping(target = "userId", source = "rental.user.id")
    @Mapping(target = "scooterId", source = "rental.scooter.id")
    @Mapping(target = "tariffId", source = "rental.tariff.id")
    @Mapping(target = "subscriptionId", source = "rental.userSubscription.id")
    RentalResponseDto mapRentalToResponseDto(Rental rental);
}
