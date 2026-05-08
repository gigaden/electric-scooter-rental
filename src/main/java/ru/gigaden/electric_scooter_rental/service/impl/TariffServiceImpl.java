package ru.gigaden.electric_scooter_rental.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.gigaden.electric_scooter_rental.entity.HourlyTariff;
import ru.gigaden.electric_scooter_rental.entity.Tariff;
import ru.gigaden.electric_scooter_rental.service.HourlyTariffService;
import ru.gigaden.electric_scooter_rental.service.TariffService;

import java.util.UUID;

/**
 * Реализация сервиса тарифов.
 * */
@Service
@RequiredArgsConstructor
@Slf4j
public class TariffServiceImpl implements TariffService {

    @Override
    public Tariff addTariff(Tariff tariff) {
        return null;
    }

    @Override
    public void deleteTariffById(UUID tariffId) {

    }

    @Override
    public Tariff findTariffByName(String tariffName) {
        return null;
    }

    @Override
    public Tariff findTariffById(UUID tariffId) {
        return null;
    }
}
