package ru.gigaden.electric_scooter_rental.service.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.gigaden.electric_scooter_rental.entity.HourlyTariff;
import ru.gigaden.electric_scooter_rental.exception.TariffNotFoundException;
import ru.gigaden.electric_scooter_rental.repository.HourlyTariffRepository;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тесты сервиса почасовых тарифов")
class HourlyTariffServiceImplTest {

  @InjectMocks
  private HourlyTariffServiceImpl hourlyTariffService;

  @Mock
  private HourlyTariffRepository hourlyTariffRepository;

  @Test
  @DisplayName("Поиск почасового тарифа - успех")
  void findHourlyTariffByTariffIdShouldBePositive() {
    UUID tariffId = UUID.randomUUID();
    HourlyTariff hourlyTariff = HourlyTariff.builder()
        .tariff(ru.gigaden.electric_scooter_rental.entity.Tariff.builder().id(tariffId).build())
        .pricePerHour(BigDecimal.valueOf(250))
        .discountPercent((short) 10)
        .build();

    when(hourlyTariffRepository.findHourlyTariffByTariffId(tariffId))
        .thenReturn(Optional.of(hourlyTariff));

    HourlyTariff result = hourlyTariffService.findHourlyTariffByTariffId(tariffId);

    assertNotNull(result);
    assertEquals(BigDecimal.valueOf(250), result.getPricePerHour());
  }

  @Test
  @DisplayName("Поиск почасового тарифа - не найден")
  void findHourlyTariffByTariffIdShouldThrowWhenNotFound() {
    UUID tariffId = UUID.randomUUID();
    when(hourlyTariffRepository.findHourlyTariffByTariffId(tariffId)).thenReturn(Optional.empty());

    assertThrows(TariffNotFoundException.class, () -> hourlyTariffService.findHourlyTariffByTariffId(tariffId));
  }
}