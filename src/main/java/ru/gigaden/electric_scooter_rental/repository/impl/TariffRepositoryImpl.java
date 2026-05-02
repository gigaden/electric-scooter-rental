package ru.gigaden.electric_scooter_rental.repository.impl;

import org.springframework.stereotype.Repository;
import ru.gigaden.electric_scooter_rental.entity.Tariff;
import ru.gigaden.electric_scooter_rental.repository.TariffRepository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class TariffRepositoryImpl extends BaseRepositoryHiber<Tariff, UUID> implements TariffRepository {

    public TariffRepositoryImpl() {
        super(Tariff.class);
    }

    @Override
    public Optional<Tariff> findTariffById(UUID tariffId) {
        return Optional.empty();
    }
}
