package ru.gigaden.electric_scooter_rental.service.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.gigaden.electric_scooter_rental.exception.TariffNotFoundException;
import ru.gigaden.electric_scooter_rental.repository.UserSubscriptionRepository;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тесты сервиса подписочных тарифов")
class SubscriptionTariffServiceImplTest {

  @InjectMocks
  private SubscriptionTariffServiceImpl subscriptionTariffService;

  @Mock
  private UserSubscriptionRepository subscriptionRepository;

  @Test
  @DisplayName("Поиск подписочного тарифа - не найден")
  void findByTariffIdShouldThrowWhenNotFound() {
    UUID tariffId = UUID.randomUUID();
    when(subscriptionRepository.findSubscriptionById(tariffId)).thenReturn(Optional.empty());

    assertThrows(TariffNotFoundException.class, () -> subscriptionTariffService.findByTariffId(tariffId));
  }
}