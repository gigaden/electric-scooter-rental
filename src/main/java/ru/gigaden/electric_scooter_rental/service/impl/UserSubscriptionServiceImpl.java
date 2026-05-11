package ru.gigaden.electric_scooter_rental.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.gigaden.electric_scooter_rental.dto.subscription.UserSubscriptionCreateDto;
import ru.gigaden.electric_scooter_rental.dto.subscription.UserSubscriptionResponseDto;
import ru.gigaden.electric_scooter_rental.entity.SubscriptionStatus;
import ru.gigaden.electric_scooter_rental.entity.Tariff;
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
import ru.gigaden.electric_scooter_rental.service.UserSubscriptionService;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Реализация сервиса управления подписками
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserSubscriptionServiceImpl implements UserSubscriptionService {

  private final UserSubscriptionRepository subscriptionRepository;
  private final UserRepository userRepository;
  private final TariffRepository tariffRepository;
  private final UserSubscriptionMapper userSubscriptionMapper;
  private final SecurityUtil securityUtil;

  /**
   * Создаёт подписку.
   *
   * @param dto - дто подписки.
   * @throws UserNotFoundException   - если пользователь не найден.
   * @throws TariffNotFoundException - если тариф не найден.
   */
  @Transactional
  @Override
  public UserSubscriptionResponseDto createSubscription(UserSubscriptionCreateDto dto) {

    UUID userId = dto.userId();
    UUID tariffId = dto.tariffId();
    LocalDateTime startDate = dto.startDate();
    LocalDateTime endDate = dto.endDate();

    User user = userRepository.findUserById(userId)
        .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));

    Tariff tariff = tariffRepository.findTariffById(tariffId)
        .filter(t -> t.getType().toString().equals("SUBSCRIPTION") && Boolean.TRUE.equals(t.getIsActive()))
        .orElseThrow(() -> new TariffNotFoundException("Недействительный подписочный тариф"));

    if (startDate.isAfter(endDate) || startDate.isBefore(LocalDateTime.now())) {
      throw new SubscriptionException("Некорректные даты подписки");
    }

    UserSubscription subscription = UserSubscription.builder()
        .user(user)
        .tariff(tariff)
        .startDate(startDate)
        .endDate(endDate)
        .status(SubscriptionStatus.ACTIVE)
        .createdOn(LocalDateTime.now())
        .build();

    UserSubscription saved = subscriptionRepository.saveSubscription(subscription);
    log.info("Создана подписка {} для пользователя {}", saved.getId(), userId);

    return userSubscriptionMapper.mapToResponseDto(saved);
  }

  /**
   * Ищет подписку по id.
   *
   * @param subscriptionId - id подписки.
   */
  @Override
  public UserSubscriptionResponseDto findSubscriptionById(UUID subscriptionId) {

    UserSubscription userSubscription = subscriptionRepository.findSubscriptionById(subscriptionId)
        .orElseThrow(() -> new SubscriptionException("Подписка не найдена"));

    if (!securityUtil.isAdmin() && !securityUtil.isOwner(userSubscription.getUser().getId())) {
      throw new AccessDeniedException("Доступ к подписке запрещён");
    }

    UserSubscriptionResponseDto response = userSubscriptionMapper.mapToResponseDto(userSubscription);
    log.debug("Нашли подписку с id = {}", subscriptionId);

    return response;


  }
}