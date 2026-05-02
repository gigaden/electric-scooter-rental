package ru.gigaden.electric_scooter_rental.repository;

import ru.gigaden.electric_scooter_rental.entity.UserSubscription;

import java.util.Optional;
import java.util.UUID;

public interface UserSubscriptionRepository {

    Optional<UserSubscription> findUserSubscriptionById(UUID subscriptionId);
}