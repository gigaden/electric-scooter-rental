package ru.gigaden.electric_scooter_rental.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.gigaden.electric_scooter_rental.entity.SubscriptionTariff;
import ru.gigaden.electric_scooter_rental.entity.UserSubscription;
import ru.gigaden.electric_scooter_rental.exception.TariffNotFoundException;
import ru.gigaden.electric_scooter_rental.repository.UserSubscriptionRepository;
import ru.gigaden.electric_scooter_rental.service.SubscriptionTariffService;
import ru.gigaden.electric_scooter_rental.entity.Tariff;

import java.util.UUID;

/**
 * Реализация сервиса подписочных тарифов.
 * */
@Service
@RequiredArgsConstructor
public class SubscriptionTariffServiceImpl implements SubscriptionTariffService {

    private final UserSubscriptionRepository subscriptionRepository;

    @Override
    public SubscriptionTariff findByTariffId(UUID tariffId) {
        return subscriptionRepository.findSubscriptionById(tariffId)
            .map(UserSubscription::getTariff)
            .map(Tariff::getSubscriptionTariff)
            .orElseThrow(() -> new TariffNotFoundException("Подписочный тариф не найден"));
    }
}
