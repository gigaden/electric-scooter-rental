package ru.gigaden.electric_scooter_rental.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.gigaden.electric_scooter_rental.dto.rental.RentalCreateDto;
import ru.gigaden.electric_scooter_rental.dto.rental.RentalResponseDto;
import ru.gigaden.electric_scooter_rental.entity.HourlyTariff;
import ru.gigaden.electric_scooter_rental.entity.Rental;
import ru.gigaden.electric_scooter_rental.entity.RentalStatus;
import ru.gigaden.electric_scooter_rental.entity.Scooter;
import ru.gigaden.electric_scooter_rental.entity.ScooterStatus;
import ru.gigaden.electric_scooter_rental.entity.SubscriptionTariff;
import ru.gigaden.electric_scooter_rental.entity.Tariff;
import ru.gigaden.electric_scooter_rental.entity.User;
import ru.gigaden.electric_scooter_rental.entity.UserSubscription;
import ru.gigaden.electric_scooter_rental.exception.RentalCompleteException;
import ru.gigaden.electric_scooter_rental.exception.RentalNotFoundException;
import ru.gigaden.electric_scooter_rental.exception.ScooterIsNotAvailableException;
import ru.gigaden.electric_scooter_rental.exception.ScooterNotFoundException;
import ru.gigaden.electric_scooter_rental.exception.SubscriptionException;
import ru.gigaden.electric_scooter_rental.exception.TariffNotFoundException;
import ru.gigaden.electric_scooter_rental.exception.UserNotFoundException;
import ru.gigaden.electric_scooter_rental.exception.UserSubscriptionNotFoundException;
import ru.gigaden.electric_scooter_rental.mapper.RentalMapper;
import ru.gigaden.electric_scooter_rental.repository.RentalRepository;
import ru.gigaden.electric_scooter_rental.repository.ScooterRepository;
import ru.gigaden.electric_scooter_rental.repository.TariffRepository;
import ru.gigaden.electric_scooter_rental.repository.UserRepository;
import ru.gigaden.electric_scooter_rental.repository.UserSubscriptionRepository;
import ru.gigaden.electric_scooter_rental.security.SecurityUtil;
import ru.gigaden.electric_scooter_rental.service.HourlyTariffService;
import ru.gigaden.electric_scooter_rental.service.RentalService;
import ru.gigaden.electric_scooter_rental.service.SubscriptionTariffService;
import ru.gigaden.electric_scooter_rental.service.TariffService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.UUID;

/**
 * Реализация сервиса аренды.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RentalServiceImpl implements RentalService {

  private final RentalRepository rentalRepository;
  private final RentalMapper rentalMapper;
  private final UserRepository userRepository;
  private final ScooterRepository scooterRepository;
  private final TariffRepository tariffRepository;
  private final UserSubscriptionRepository subscriptionRepository;
  private final TariffService tariffService;
  private final HourlyTariffService hourlyTariffService;
  private final SubscriptionTariffService subscriptionTariffService;
  private final SecurityUtil securityUtil;

  @Transactional
  @Override
  public RentalResponseDto createRental(RentalCreateDto dto) {

    Rental newRental = buildRentalFromDto(dto);
    Scooter scooter = newRental.getScooter();

    Rental savedRental = rentalRepository.createRental(newRental);
    scooter.setStatus(ScooterStatus.RENTED);
    scooterRepository.updateScooter(scooter);

    RentalResponseDto response = rentalMapper.mapRentalToResponseDto(savedRental);
    log.info("Создана новая аренда с id = {}", savedRental.getId());

    return response;
  }

  /**
   * Ищем аренду по id.
   *
   * @param rentalId - id аренды
   * @return - дто аренды
   * @throws RentalNotFoundException - если аренда не найдена
   */
  @Override
  public RentalResponseDto findRentalById(UUID rentalId) {

    Rental rental = getRowRentalOrThrow(rentalId);

    if (!securityUtil.isAdmin() && !securityUtil.isOwner(rental.getUser().getId())) {
      throw new AccessDeniedException("Доступ к аренде запрещён");
    }

    RentalResponseDto response = rentalMapper.mapRentalToResponseDto(rental);
    log.debug("Получили аренду с id = {}", rentalId);

    return response;
  }

  /**
   * Ищем все аренды.
   *
   * @param size - размер списка
   * @param page - начальная страница
   * @return - список аренд
   */
  @Override
  public Collection<RentalResponseDto> findAllRentals(int page, int size) {

    Collection<RentalResponseDto> response = rentalRepository.findAllRentals(page, size).stream()
        .map(rentalMapper::mapRentalToResponseDto)
        .toList();
    log.info("Нашли все аренды page = {} size = {}", page, size);

    return response;
  }

  /**
   * Завершает аренду.
   */
  @Transactional
  @Override
  public RentalResponseDto completeRental(UUID rentalId) {

    Rental rental = getRowRentalOrThrow(rentalId);

    if (!securityUtil.isAdmin() && !securityUtil.isOwner(rental.getUser().getId())) {
      throw new AccessDeniedException("Завершить аренду может только её владелец или администратор");
    }

    if (rental.getStatus() != RentalStatus.IN_PROGRESS) {
      throw new RentalCompleteException("Аренда с id = %s не является активной".formatted(rentalId));
    }

    rental.setEndMileage(rental.getScooter().getMileage());
    rental.setEndDate(LocalDateTime.now());
    rental.setStatus(RentalStatus.FINISHED);

    BigDecimal cost = calculateRentalCost(rental);
    rental.setRentalCost(cost);

    Rental updatedRental = rentalRepository.updateRental(rental);

    Scooter scooter = updatedRental.getScooter();
    scooter.setStatus(ScooterStatus.AVAILABLE);
    scooterRepository.updateScooter(scooter);

    RentalResponseDto response = rentalMapper.mapRentalToResponseDto(updatedRental);
    log.info("Аренда с id = {} завершена", rentalId);

    return response;
  }

  @Override
  public Collection<RentalResponseDto> findRentalsByUserId(UUID userId, int page, int size) {

    Collection<RentalResponseDto> response = rentalRepository.findRentalsByUserId(userId, page, size).stream()
        .map(rentalMapper::mapRentalToResponseDto)
        .toList();
    log.debug("Получили историю аренды пользователя {} в количестве {}", userId, response.size());
    return response;
  }

  @Override
  public Collection<RentalResponseDto> findFinishedRentalsByScooterId(UUID scooterId, int page, int size) {

    Collection<RentalResponseDto> response = rentalRepository
        .findFinishedRentalsByScooterId(scooterId, page, size).stream()
        .map(rentalMapper::mapRentalToResponseDto)
        .toList();
    log.debug("Получили завершённые аренды самоката {} в количестве {}", scooterId, response.size());

    return response;
  }

  /**
   * Вычисляет стоимость аренды.
   */
  private BigDecimal calculateRentalCost(Rental rental) {

    LocalDateTime endTime = rental.getEndDate() != null ? rental.getEndDate() : LocalDateTime.now();

    if (rental.getTariff() != null) {
      return calculateWithTariff(rental, endTime);
    } else if (rental.getUserSubscription() != null) {
      return calculateWithSubscription(rental, endTime);
    } else {
      return calculateDefaultTariff(rental, endTime);
    }
  }

  /**
   * Вычисляет стоимость аренды по дефолтному тарифу.
   */
  private BigDecimal calculateDefaultTariff(Rental rental, LocalDateTime endTime) {

    Tariff defaultTariff = tariffService.findTariffByName("Default Hourly Tariff");
    HourlyTariff hourlyTariff = hourlyTariffService.findHourlyTariffByTariffId(defaultTariff.getId());

    Duration duration = Duration.between(rental.getStartDate(), endTime);
    BigDecimal hours = new BigDecimal(duration.toMinutes())
        .divide(BigDecimal.valueOf(60), 4, RoundingMode.HALF_UP);

    BigDecimal baseCost = hours.multiply(hourlyTariff.getPricePerHour());
    return applyDiscount(baseCost, hourlyTariff.getDiscountPercent());
  }

  /**
   * Вычисляет аренду по тарифу.
   */
  private BigDecimal calculateWithTariff(Rental rental, LocalDateTime endTime) {

    HourlyTariff hourlyTariff = hourlyTariffService.findHourlyTariffByTariffId(rental.getTariff().getId());

    Duration duration = Duration.between(rental.getStartDate(), endTime);
    BigDecimal hours = BigDecimal.valueOf(duration.toMinutes())
        .divide(BigDecimal.valueOf(60), 4, RoundingMode.HALF_UP);

    BigDecimal baseCost = hours.multiply(hourlyTariff.getPricePerHour());
    return applyDiscount(baseCost, hourlyTariff.getDiscountPercent());
  }

  /**
   * Вычисляет стоимость аренды с учётом подписки.
   */
  private BigDecimal calculateWithSubscription(Rental rental, LocalDateTime endTime) {

    UserSubscription subscription = rental.getUserSubscription();

    SubscriptionTariff subTariff = subscriptionTariffService.findByTariffId(subscription.getTariff().getId());

    if (rental.getStartDate().isAfter(subscription.getEndDate())) {
      throw new SubscriptionException("Аренда началась после окончания подписки");
    }
    if (endTime.isAfter(subscription.getEndDate())) {
      endTime = subscription.getEndDate();
    }

    Duration duration = Duration.between(rental.getStartDate(), endTime);
    BigDecimal hours = BigDecimal.valueOf(duration.toMinutes())
        .divide(BigDecimal.valueOf(60), 4, RoundingMode.HALF_UP);

    BigDecimal baseCost = hours.multiply(subTariff.getPrice());
    return applyDiscount(baseCost, subTariff.getDiscountPercent());
  }

  /**
   * Вычисляет аренду с учётом скидки.
   */
  private BigDecimal applyDiscount(BigDecimal baseCost, Short discountPercent) {

    if (discountPercent == null || discountPercent <= 0) {
      return baseCost;
    }

    BigDecimal discount = baseCost
        .multiply(BigDecimal.valueOf(discountPercent))
        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

    return baseCost.subtract(discount);
  }

  /**
   * Ищем незамапенную аренда по id.
   */
  private Rental getRowRentalOrThrow(UUID rentalId) {
    return rentalRepository.findRentalById(rentalId)
        .orElseThrow(() -> new RentalNotFoundException("Аренда с id = %s не найдена".formatted(rentalId)));
  }

  /**
   * Собираем новую аренду из разных сущностей.
   */
  private Rental buildRentalFromDto(RentalCreateDto dto) {

    User user = userRepository.findUserById(dto.userId())
        .orElseThrow(() -> new UserNotFoundException("Пользователь с id = %s не найден"
            .formatted(dto.userId())));

    Scooter scooter = scooterRepository.findScooterById(dto.scooterId())
        .orElseThrow(() -> new ScooterNotFoundException("Самокат с id = %s не найден"
            .formatted(dto.scooterId())));

    if (!scooter.getStatus().equals(ScooterStatus.AVAILABLE)) {
      throw new ScooterIsNotAvailableException("Самокат с id = %s занят".formatted(scooter.getId()));
    }

    Tariff tariff = null;
    if (dto.tariffId() != null) {
      tariff = tariffRepository.findTariffById(dto.tariffId())
          .orElseThrow(() -> new TariffNotFoundException("Тариф с id = %s не найден"
              .formatted(dto.tariffId())));
    }

    UserSubscription subscription = null;
    if (dto.userSubscriptionId() != null) {
      subscription = subscriptionRepository.findSubscriptionById(dto.userSubscriptionId())
          .orElseThrow(() -> new UserSubscriptionNotFoundException("Подписка с id = %s не найден"
              .formatted(dto.userSubscriptionId())));
    }

    return Rental.builder()
        .user(user)
        .scooter(scooter)
        .status(RentalStatus.IN_PROGRESS)
        .startDate(LocalDateTime.now())
        .startMileage(dto.startMileage())
        .tariff(tariff)
        .userSubscription(subscription)
        .build();
  }
}
