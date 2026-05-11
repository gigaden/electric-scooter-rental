package ru.gigaden.electric_scooter_rental.repository.impl;

import org.springframework.stereotype.Repository;
import ru.gigaden.electric_scooter_rental.entity.UserSubscription;
import ru.gigaden.electric_scooter_rental.repository.UserSubscriptionRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * Репозиторий для подписок пользователей.
 */
@Repository
public class UserSubscriptionRepositoryImpl extends BaseRepositoryHiber<UserSubscription, UUID> implements UserSubscriptionRepository {

  public UserSubscriptionRepositoryImpl() {
    super(UserSubscription.class);
  }

  @Override
  public Optional<UserSubscription> findSubscriptionById(UUID subscriptionId) {
    return Optional.ofNullable(find(subscriptionId));
  }

  @Override
  public UserSubscription saveSubscription(UserSubscription subscription) {
    return save(subscription);
  }
}