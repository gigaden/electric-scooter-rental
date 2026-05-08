package ru.gigaden.electric_scooter_rental.service;

import ru.gigaden.electric_scooter_rental.entity.UserSubscription;

import java.util.UUID;

public interface UserSubscriptionService {

    UserSubscription findSubscriptionById(UUID subscriptionId);
}
