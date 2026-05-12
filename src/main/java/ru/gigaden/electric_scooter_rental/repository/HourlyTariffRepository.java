package ru.gigaden.electric_scooter_rental.repository;

import ru.gigaden.electric_scooter_rental.entity.HourlyTariff;

import java.util.Optional;
import java.util.UUID;

/**
 * Репозиторий для часовых тарифов
 */
public interface HourlyTariffRepository {

  Optional<HourlyTariff> findHourlyTariffByTariffId(UUID tariffId);
}
