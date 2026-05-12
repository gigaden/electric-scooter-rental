package ru.gigaden.electric_scooter_rental.service.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.gigaden.electric_scooter_rental.entity.Tariff;
import ru.gigaden.electric_scooter_rental.entity.TariffType;
import ru.gigaden.electric_scooter_rental.exception.TariffNotFoundException;
import ru.gigaden.electric_scooter_rental.repository.TariffRepository;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тесты сервиса тарифов")
class TariffServiceImplTest {

  @InjectMocks
  private TariffServiceImpl tariffService;

  @Mock
  private TariffRepository tariffRepository;

  @Test
  @DisplayName("Добавление тарифа - успех")
  void addTariffShouldBePositive() {
    Tariff tariff = Tariff.builder()
        .id(UUID.randomUUID())
        .name("Premium")
        .type(TariffType.HOURLY)
        .isActive(true)
        .build();

    when(tariffRepository.addTariff(any(Tariff.class))).thenReturn(tariff);

    Tariff result = tariffService.addTariff(tariff);

    assertNotNull(result);
    assertEquals("Premium", result.getName());
    verify(tariffRepository).addTariff(tariff);
  }

  @Test
  @DisplayName("Удаление тарифа - успех")
  void deleteTariffByIdShouldBePositive() {
    UUID tariffId = UUID.randomUUID();

    tariffService.deleteTariffById(tariffId);

    verify(tariffRepository).deleteTariffById(tariffId);
  }

  @Test
  @DisplayName("Поиск тарифа по имени - успех")
  void findTariffByNameShouldBePositive() {
    Tariff tariff = Tariff.builder()
        .id(UUID.randomUUID())
        .name("Default Hourly Tariff")
        .type(TariffType.HOURLY)
        .isActive(true)
        .build();

    when(tariffRepository.findTariffByName("Default Hourly Tariff")).thenReturn(Optional.of(tariff));

    Tariff result = tariffService.findTariffByName("Default Hourly Tariff");

    assertNotNull(result);
    assertEquals("Default Hourly Tariff", result.getName());
  }

  @Test
  @DisplayName("Поиск тарифа по имени - не найден")
  void findTariffByNameShouldThrowWhenNotFound() {
    when(tariffRepository.findTariffByName("Unknown")).thenReturn(Optional.empty());

    assertThrows(TariffNotFoundException.class, () -> tariffService.findTariffByName("Unknown"));
  }

  @Test
  @DisplayName("Поиск тарифа по ID - успех")
  void findTariffByIdShouldBePositive() {
    UUID tariffId = UUID.randomUUID();
    Tariff tariff = Tariff.builder().id(tariffId).name("Test").build();

    when(tariffRepository.findTariffById(tariffId)).thenReturn(Optional.of(tariff));

    Tariff result = tariffService.findTariffById(tariffId);

    assertNotNull(result);
    assertEquals(tariffId, result.getId());
  }

  @Test
  @DisplayName("Поиск тарифа по ID - не найден")
  void findTariffByIdShouldThrowWhenNotFound() {
    UUID tariffId = UUID.randomUUID();
    when(tariffRepository.findTariffById(tariffId)).thenReturn(Optional.empty());

    assertThrows(TariffNotFoundException.class, () -> tariffService.findTariffById(tariffId));
  }
}