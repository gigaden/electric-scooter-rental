package ru.gigaden.electric_scooter_rental.service;

import ru.gigaden.electric_scooter_rental.entity.SubscriptionTariff;

import java.util.UUID;

public interface SubscriptionTariffService {

  SubscriptionTariff findByTariffId(UUID tariffId);
}
