package ru.gigaden.electric_scooter_rental.service;

import ru.gigaden.electric_scooter_rental.entity.Tariff;

import java.util.UUID;

public interface TariffService {

  Tariff addTariff(Tariff tariff);

  void deleteTariffById(UUID tariffId);

  Tariff findTariffByName(String tariffName);

  Tariff findTariffById(UUID tariffId);
}
