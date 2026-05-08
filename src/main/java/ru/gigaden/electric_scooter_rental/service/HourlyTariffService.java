package ru.gigaden.electric_scooter_rental.service;

import ru.gigaden.electric_scooter_rental.entity.HourlyTariff;
import ru.gigaden.electric_scooter_rental.entity.Tariff;

import java.util.UUID;

public interface HourlyTariffService {

    HourlyTariff findHourlyTariffByTariffId(UUID tariffId);
}
