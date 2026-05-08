package ru.gigaden.electric_scooter_rental.repository;

import ru.gigaden.electric_scooter_rental.entity.HourlyTariff;
import ru.gigaden.electric_scooter_rental.entity.Tariff;

import java.util.Optional;
import java.util.UUID;

public interface HourlyTariffRepository {

    Optional<HourlyTariff> findHourlyTariffByTariffId(UUID tariffId);
}
