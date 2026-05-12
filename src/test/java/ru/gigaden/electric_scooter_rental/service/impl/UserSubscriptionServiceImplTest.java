package ru.gigaden.electric_scooter_rental.service.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import ru.gigaden.electric_scooter_rental.dto.subscription.UserSubscriptionCreateDto;
import ru.gigaden.electric_scooter_rental.dto.subscription.UserSubscriptionResponseDto;
import ru.gigaden.electric_scooter_rental.entity.SubscriptionStatus;
import ru.gigaden.electric_scooter_rental.entity.Tariff;
import ru.gigaden.electric_scooter_rental.entity.TariffType;
import ru.gigaden.electric_scooter_rental.entity.User;
import ru.gigaden.electric_scooter_rental.entity.UserSubscription;
import ru.gigaden.electric_scooter_rental.exception.SubscriptionException;
import ru.gigaden.electric_scooter_rental.exception.TariffNotFoundException;
import ru.gigaden.electric_scooter_rental.exception.UserNotFoundException;
import ru.gigaden.electric_scooter_rental.mapper.UserSubscriptionMapper;
import ru.gigaden.electric_scooter_rental.repository.TariffRepository;
import ru.gigaden.electric_scooter_rental.repository.UserRepository;
import ru.gigaden.electric_scooter_rental.repository.UserSubscriptionRepository;
import ru.gigaden.electric_scooter_rental.security.SecurityUtil;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тесты сервиса подписок")
class UserSubscriptionServiceImplTest {

  @InjectMocks
  private UserSubscriptionServiceImpl subscriptionService;

  @Mock
  private UserSubscriptionRepository subscriptionRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private TariffRepository tariffRepository;

  @Mock
  private UserSubscriptionMapper userSubscriptionMapper;

  @Mock
  private SecurityUtil securityUtil;

  private static final UUID USER_ID = UUID.randomUUID();
  private static final UUID TARIFF_ID = UUID.randomUUID();
  private static final UUID SUBSCRIPTION_ID = UUID.randomUUID();

  @Test
  @DisplayName("Создание подписки - успех")
  void createSubscriptionShouldBePositive() {
    UserSubscriptionCreateDto dto = new UserSubscriptionCreateDto(
        USER_ID, TARIFF_ID,
        LocalDateTime.now().plusDays(1),
        LocalDateTime.now().plusDays(30)
    );

    User user = User.builder().id(USER_ID).username("testuser").build();
    Tariff tariff = Tariff.builder()
        .id(TARIFF_ID)
        .name("Monthly")
        .type(TariffType.SUBSCRIPTION)
        .isActive(true)
        .build();

    UserSubscription subscription = UserSubscription.builder()
        .id(SUBSCRIPTION_ID)
        .user(user)
        .tariff(tariff)
        .startDate(dto.startDate())
        .endDate(dto.endDate())
        .status(SubscriptionStatus.ACTIVE)
        .createdOn(LocalDateTime.now())
        .build();

    UserSubscriptionResponseDto responseDto = new UserSubscriptionResponseDto(
        SUBSCRIPTION_ID, USER_ID, TARIFF_ID,
        SubscriptionStatus.ACTIVE, dto.startDate(), dto.endDate(), LocalDateTime.now()
    );

    when(userRepository.findUserById(USER_ID)).thenReturn(Optional.of(user));
    when(tariffRepository.findTariffById(TARIFF_ID)).thenReturn(Optional.of(tariff));
    when(subscriptionRepository.saveSubscription(any(UserSubscription.class))).thenReturn(subscription);
    when(userSubscriptionMapper.mapToResponseDto(subscription)).thenReturn(responseDto);

    UserSubscriptionResponseDto result = subscriptionService.createSubscription(dto);

    assertNotNull(result);
    assertEquals(SUBSCRIPTION_ID, result.id());
    verify(subscriptionRepository).saveSubscription(any(UserSubscription.class));
  }

  @Test
  @DisplayName("Создание подписки - пользователь не найден")
  void createSubscriptionShouldThrowWhenUserNotFound() {
    UserSubscriptionCreateDto dto = new UserSubscriptionCreateDto(
        USER_ID, TARIFF_ID,
        LocalDateTime.now().plusDays(1),
        LocalDateTime.now().plusDays(30)
    );

    when(userRepository.findUserById(USER_ID)).thenReturn(Optional.empty());

    assertThrows(UserNotFoundException.class, () -> subscriptionService.createSubscription(dto));
    verify(subscriptionRepository, never()).saveSubscription(any());
  }

  @Test
  @DisplayName("Создание подписки - тариф не найден или не подписочный")
  void createSubscriptionShouldThrowWhenTariffInvalid() {
    UserSubscriptionCreateDto dto = new UserSubscriptionCreateDto(
        USER_ID, TARIFF_ID,
        LocalDateTime.now().plusDays(1),
        LocalDateTime.now().plusDays(30)
    );

    Tariff hourlyTariff = Tariff.builder()
        .id(TARIFF_ID)
        .type(TariffType.HOURLY)
        .isActive(true)
        .build();

    when(userRepository.findUserById(USER_ID)).thenReturn(Optional.of(User.builder().id(USER_ID).build()));
    when(tariffRepository.findTariffById(TARIFF_ID)).thenReturn(Optional.of(hourlyTariff));

    assertThrows(TariffNotFoundException.class, () -> subscriptionService.createSubscription(dto));
  }

  @Test
  @DisplayName("Создание подписки - некорректные даты")
  void createSubscriptionShouldThrowWhenDatesInvalid() {
    UserSubscriptionCreateDto dto = new UserSubscriptionCreateDto(
        USER_ID, TARIFF_ID,
        LocalDateTime.now().plusDays(30),
        LocalDateTime.now().plusDays(1)
    );

    when(userRepository.findUserById(USER_ID)).thenReturn(Optional.of(User.builder().id(USER_ID).build()));
    when(tariffRepository.findTariffById(TARIFF_ID))
        .thenReturn(Optional.of(Tariff.builder().id(TARIFF_ID).type(TariffType.SUBSCRIPTION).isActive(true).build()));

    assertThrows(SubscriptionException.class, () -> subscriptionService.createSubscription(dto));
  }

  @Test
  @DisplayName("Получение подписки - владелец или админ")
  void findSubscriptionByIdShouldBePositive() {
    User owner = User.builder().id(USER_ID).build();
    UserSubscription subscription = UserSubscription.builder()
        .id(SUBSCRIPTION_ID)
        .user(owner)
        .tariff(Tariff.builder().id(TARIFF_ID).build())
        .status(SubscriptionStatus.ACTIVE)
        .build();

    UserSubscriptionResponseDto responseDto = new UserSubscriptionResponseDto(
        SUBSCRIPTION_ID, USER_ID, TARIFF_ID,
        SubscriptionStatus.ACTIVE, LocalDateTime.now(), LocalDateTime.now().plusDays(30), LocalDateTime.now()
    );

    when(subscriptionRepository.findSubscriptionById(SUBSCRIPTION_ID)).thenReturn(Optional.of(subscription));
    when(securityUtil.isAdmin()).thenReturn(false);
    when(securityUtil.isOwner(USER_ID)).thenReturn(true);
    when(userSubscriptionMapper.mapToResponseDto(subscription)).thenReturn(responseDto);

    UserSubscriptionResponseDto result = subscriptionService.findSubscriptionById(SUBSCRIPTION_ID);

    assertNotNull(result);
    assertEquals(SUBSCRIPTION_ID, result.id());
  }

  @Test
  @DisplayName("Получение подписки - доступ запрещён")
  void findSubscriptionByIdShouldThrowWhenAccessDenied() {
    User otherUser = User.builder().id(UUID.randomUUID()).build();
    UserSubscription subscription = UserSubscription.builder()
        .id(SUBSCRIPTION_ID)
        .user(otherUser)
        .build();

    when(subscriptionRepository.findSubscriptionById(SUBSCRIPTION_ID)).thenReturn(Optional.of(subscription));
    when(securityUtil.isAdmin()).thenReturn(false);
    when(securityUtil.isOwner(otherUser.getId())).thenReturn(false);

    assertThrows(AccessDeniedException.class, () -> subscriptionService.findSubscriptionById(SUBSCRIPTION_ID));
  }

  @Test
  @DisplayName("Получение подписки - не найдена")
  void findSubscriptionByIdShouldThrowWhenNotFound() {
    when(subscriptionRepository.findSubscriptionById(SUBSCRIPTION_ID)).thenReturn(Optional.empty());

    assertThrows(SubscriptionException.class, () -> subscriptionService.findSubscriptionById(SUBSCRIPTION_ID));
  }
}