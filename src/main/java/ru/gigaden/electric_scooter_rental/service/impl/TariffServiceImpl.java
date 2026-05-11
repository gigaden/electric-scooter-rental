package ru.gigaden.electric_scooter_rental.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.gigaden.electric_scooter_rental.entity.Tariff;
import ru.gigaden.electric_scooter_rental.exception.TariffNotFoundException;
import ru.gigaden.electric_scooter_rental.repository.TariffRepository;
import ru.gigaden.electric_scooter_rental.service.TariffService;

import java.util.UUID;

/**
 * Реализация сервиса тарифов.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TariffServiceImpl implements TariffService {

  private final TariffRepository tariffRepository;

  @Transactional
  @Override
  public Tariff addTariff(Tariff tariff) {

    Tariff saved = tariffRepository.addTariff(tariff);
    log.info("Добавлен тариф {}", saved.getName());

    return saved;
  }

  @Transactional
  @Override
  public void deleteTariffById(UUID tariffId) {

    tariffRepository.deleteTariffById(tariffId);
    log.info("Удалён тариф с id {}", tariffId);
  }

  @Override
  public Tariff findTariffByName(String tariffName) {

    return tariffRepository.findTariffByName(tariffName)
        .orElseThrow(() -> new TariffNotFoundException("Тариф не найден: " + tariffName));
  }

  @Override
  public Tariff findTariffById(UUID tariffId) {

    return tariffRepository.findTariffById(tariffId)
        .orElseThrow(() -> new TariffNotFoundException("Тариф не найден"));
  }
}