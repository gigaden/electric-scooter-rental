package ru.gigaden.electric_scooter_rental.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.gigaden.electric_scooter_rental.dto.subscription.UserSubscriptionResponseDto;
import ru.gigaden.electric_scooter_rental.entity.UserSubscription;

@Mapper(componentModel = "spring")
public interface UserSubscriptionMapper {

    @Mapping(target = "userId", source = "userSubscription.user.id")
    @Mapping(target = "tariffId", source = "userSubscription.tariff.id")
    UserSubscriptionResponseDto mapToResponseDto(UserSubscription userSubscription);
}