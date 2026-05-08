package ru.gigaden.electric_scooter_rental.repository.impl;

import org.springframework.stereotype.Repository;
import ru.gigaden.electric_scooter_rental.entity.HourlyTariff;
import ru.gigaden.electric_scooter_rental.repository.HourlyTariffRepository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class HourlyTariffRepositoryImpl extends BaseRepositoryHiber<HourlyTariff, UUID> implements HourlyTariffRepository {

    public HourlyTariffRepositoryImpl() {
        super(HourlyTariff.class);
    }

    @Override
    public Optional<HourlyTariff> findHourlyTariffByTariffId(UUID tariffId) {
        return Optional.empty();
    }
}
