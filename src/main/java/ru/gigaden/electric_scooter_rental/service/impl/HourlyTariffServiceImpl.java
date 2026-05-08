package ru.gigaden.electric_scooter_rental.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.gigaden.electric_scooter_rental.entity.HourlyTariff;
import ru.gigaden.electric_scooter_rental.service.HourlyTariffService;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class HourlyTariffServiceImpl implements HourlyTariffService {
    @Override
    public HourlyTariff findHourlyTariffByTariffId(UUID tariffId) {
        return null;
    }
}
