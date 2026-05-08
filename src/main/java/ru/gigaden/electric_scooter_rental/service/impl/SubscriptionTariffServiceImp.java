package ru.gigaden.electric_scooter_rental.service.impl;

import org.springframework.stereotype.Service;
import ru.gigaden.electric_scooter_rental.entity.SubscriptionTariff;
import ru.gigaden.electric_scooter_rental.service.SubscriptionTariffService;

import java.util.UUID;

@Service
public class SubscriptionTariffServiceImp implements SubscriptionTariffService {
    @Override
    public SubscriptionTariff findByTariffId(UUID tariffId) {
        return null;
    }
}
