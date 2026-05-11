package ru.gigaden.electric_scooter_rental.service;

import ru.gigaden.electric_scooter_rental.dto.subscription.UserSubscriptionCreateDto;
import ru.gigaden.electric_scooter_rental.dto.subscription.UserSubscriptionResponseDto;
import ru.gigaden.electric_scooter_rental.entity.UserSubscription;

import java.time.LocalDateTime;
import java.util.UUID;

public interface UserSubscriptionService {

    UserSubscriptionResponseDto createSubscription(UserSubscriptionCreateDto dto);

    UserSubscriptionResponseDto findSubscriptionById(UUID subscriptionId);
}
