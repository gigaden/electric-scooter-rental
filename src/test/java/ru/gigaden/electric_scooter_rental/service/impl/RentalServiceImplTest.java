package ru.gigaden.electric_scooter_rental.service.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import ru.gigaden.electric_scooter_rental.dto.rental.RentalCreateDto;
import ru.gigaden.electric_scooter_rental.dto.rental.RentalResponseDto;
import ru.gigaden.electric_scooter_rental.entity.HourlyTariff;
import ru.gigaden.electric_scooter_rental.entity.Rental;
import ru.gigaden.electric_scooter_rental.entity.RentalPoint;
import ru.gigaden.electric_scooter_rental.entity.RentalStatus;
import ru.gigaden.electric_scooter_rental.entity.Scooter;
import ru.gigaden.electric_scooter_rental.entity.ScooterStatus;
import ru.gigaden.electric_scooter_rental.entity.Tariff;
import ru.gigaden.electric_scooter_rental.entity.User;
import ru.gigaden.electric_scooter_rental.exception.RentalCompleteException;
import ru.gigaden.electric_scooter_rental.exception.ScooterIsNotAvailableException;
import ru.gigaden.electric_scooter_rental.mapper.RentalMapper;
import ru.gigaden.electric_scooter_rental.repository.RentalRepository;
import ru.gigaden.electric_scooter_rental.repository.ScooterRepository;
import ru.gigaden.electric_scooter_rental.repository.TariffRepository;
import ru.gigaden.electric_scooter_rental.repository.UserRepository;
import ru.gigaden.electric_scooter_rental.repository.UserSubscriptionRepository;
import ru.gigaden.electric_scooter_rental.security.SecurityUtil;
import ru.gigaden.electric_scooter_rental.service.HourlyTariffService;
import ru.gigaden.electric_scooter_rental.service.SubscriptionTariffService;
import ru.gigaden.electric_scooter_rental.service.TariffService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тесты сервиса аренды")
class RentalServiceImplTest {

  @InjectMocks
  private RentalServiceImpl rentalService;

  @Mock
  private RentalRepository rentalRepository;

  @Mock
  private RentalMapper rentalMapper;

  @Mock
  private UserRepository userRepository;

  @Mock
  private ScooterRepository scooterRepository;

  @Mock
  private TariffRepository tariffRepository;

  @Mock
  private UserSubscriptionRepository subscriptionRepository;

  @Mock
  private TariffService tariffService;

  @Mock
  private HourlyTariffService hourlyTariffService;

  @Mock
  private SubscriptionTariffService subscriptionTariffService;

  @Mock
  private SecurityUtil securityUtil;

  private static final UUID USER_ID = UUID.randomUUID();
  private static final UUID SCOOTER_ID = UUID.randomUUID();
  private static final UUID RENTAL_ID = UUID.randomUUID();
  private static final UUID TARIFF_ID = UUID.randomUUID();

  @Test
  @DisplayName("Создание аренды - успех")
  void createRentalShouldBePositive() {
    RentalCreateDto dto = new RentalCreateDto(USER_ID, SCOOTER_ID, 100, null, null);

    User user = User.builder().id(USER_ID).build();
    RentalPoint point = RentalPoint.builder().id(UUID.randomUUID()).latitude(56.0).longitude(44.0).build();
    Scooter scooter = Scooter.builder()
        .id(SCOOTER_ID)
        .rentalPoint(point)
        .status(ScooterStatus.AVAILABLE)
        .mileage(100)
        .build();

    Rental rental = Rental.builder()
        .user(user)
        .scooter(scooter)
        .status(RentalStatus.IN_PROGRESS)
        .startDate(LocalDateTime.now())
        .startMileage(100)
        .build();

    Rental savedRental = Rental.builder().id(RENTAL_ID).user(user).scooter(scooter).build();

    RentalResponseDto responseDto = new RentalResponseDto(
        RENTAL_ID, USER_ID, SCOOTER_ID, RentalStatus.IN_PROGRESS,
        LocalDateTime.now(), null, 100, null, null, null, null
    );

    when(userRepository.findUserById(USER_ID)).thenReturn(Optional.of(user));
    when(scooterRepository.findScooterById(SCOOTER_ID)).thenReturn(Optional.of(scooter));
    when(rentalRepository.createRental(any(Rental.class))).thenReturn(savedRental);
    when(rentalMapper.mapRentalToResponseDto(savedRental)).thenReturn(responseDto);

    RentalResponseDto result = rentalService.createRental(dto);

    assertNotNull(result);
    assertEquals(RENTAL_ID, result.id());
    verify(scooterRepository).updateScooter(scooter);
    assertEquals(ScooterStatus.RENTED, scooter.getStatus());
  }

  @Test
  @DisplayName("Создание аренды - самокат занят")
  void createRentalShouldThrowWhenScooterNotAvailable() {
    RentalCreateDto dto = new RentalCreateDto(USER_ID, SCOOTER_ID, 100, null, null);

    Scooter rentedScooter = Scooter.builder()
        .id(SCOOTER_ID)
        .status(ScooterStatus.RENTED)
        .build();

    when(userRepository.findUserById(USER_ID)).thenReturn(Optional.of(User.builder().id(USER_ID).build()));
    when(scooterRepository.findScooterById(SCOOTER_ID)).thenReturn(Optional.of(rentedScooter));

    assertThrows(ScooterIsNotAvailableException.class, () -> rentalService.createRental(dto));
    verify(rentalRepository, never()).createRental(any());
  }

  @Test
  @DisplayName("Получение аренды - владелец или админ")
  void findRentalByIdShouldBePositive() {
    User owner = User.builder().id(USER_ID).build();
    Rental rental = Rental.builder().id(RENTAL_ID).user(owner).build();
    RentalResponseDto responseDto = new RentalResponseDto(
        RENTAL_ID, USER_ID, SCOOTER_ID, RentalStatus.IN_PROGRESS,
        LocalDateTime.now(), null, 100, null, null, null, null
    );

    when(rentalRepository.findRentalById(RENTAL_ID)).thenReturn(Optional.of(rental));
    when(securityUtil.isAdmin()).thenReturn(false);
    when(securityUtil.isOwner(USER_ID)).thenReturn(true);
    when(rentalMapper.mapRentalToResponseDto(rental)).thenReturn(responseDto);

    RentalResponseDto result = rentalService.findRentalById(RENTAL_ID);

    assertNotNull(result);
    assertEquals(RENTAL_ID, result.id());
  }

  @Test
  @DisplayName("Получение аренды - доступ запрещён")
  void findRentalByIdShouldThrowWhenAccessDenied() {
    User otherUser = User.builder().id(UUID.randomUUID()).build();
    Rental rental = Rental.builder().id(RENTAL_ID).user(otherUser).build();

    when(rentalRepository.findRentalById(RENTAL_ID)).thenReturn(Optional.of(rental));
    when(securityUtil.isAdmin()).thenReturn(false);
    when(securityUtil.isOwner(otherUser.getId())).thenReturn(false);

    assertThrows(AccessDeniedException.class, () -> rentalService.findRentalById(RENTAL_ID));
  }

  @Test
  @DisplayName("Завершение аренды - успех")
  void completeRentalShouldBePositive() {
    User owner = User.builder().id(USER_ID).build();
    Scooter scooter = Scooter.builder().id(SCOOTER_ID).mileage(150).build();
    Rental rental = Rental.builder()
        .id(RENTAL_ID)
        .user(owner)
        .scooter(scooter)
        .status(RentalStatus.IN_PROGRESS)
        .startDate(LocalDateTime.now().minusHours(2))
        .startMileage(100)
        .build();

    Rental updatedRental = Rental.builder()
        .id(RENTAL_ID)
        .user(owner)
        .scooter(scooter)  // ✅ Теперь скутер установлен
        .status(RentalStatus.FINISHED)
        .endMileage(150)
        .rentalCost(BigDecimal.valueOf(500))
        .build();

    RentalResponseDto responseDto = new RentalResponseDto(
        RENTAL_ID, USER_ID, SCOOTER_ID, RentalStatus.FINISHED,
        rental.getStartDate(), LocalDateTime.now(), 100, 150, null, null, BigDecimal.valueOf(500)
    );

    when(rentalRepository.findRentalById(RENTAL_ID)).thenReturn(Optional.of(rental));
    when(securityUtil.isAdmin()).thenReturn(true);
    when(tariffService.findTariffByName("Default Hourly Tariff"))
        .thenReturn(Tariff.builder().id(TARIFF_ID).build());
    when(hourlyTariffService.findHourlyTariffByTariffId(TARIFF_ID))
        .thenReturn(HourlyTariff.builder().pricePerHour(BigDecimal.valueOf(250)).build());
    when(rentalRepository.updateRental(any(Rental.class))).thenReturn(updatedRental);
    when(rentalMapper.mapRentalToResponseDto(updatedRental)).thenReturn(responseDto);

    RentalResponseDto result = rentalService.completeRental(RENTAL_ID);

    assertNotNull(result);
    assertEquals(RentalStatus.FINISHED, result.status());
    assertEquals(ScooterStatus.AVAILABLE, scooter.getStatus());
  }

  @Test
  @DisplayName("Завершение аренды - уже завершена")
  void completeRentalShouldThrowWhenAlreadyFinished() {
    Rental finishedRental = Rental.builder()
        .id(RENTAL_ID)
        .status(RentalStatus.FINISHED)
        .user(User.builder().id(USER_ID).build())
        .build();

    when(rentalRepository.findRentalById(RENTAL_ID)).thenReturn(Optional.of(finishedRental));
    when(securityUtil.isAdmin()).thenReturn(true);

    assertThrows(RentalCompleteException.class, () -> rentalService.completeRental(RENTAL_ID));
  }

  @Test
  @DisplayName("Получение всех аренд - успех")
  void findAllRentalsShouldBePositive() {
    Rental rental = Rental.builder().id(RENTAL_ID).build();
    RentalResponseDto dto = new RentalResponseDto(
        RENTAL_ID, USER_ID, SCOOTER_ID, RentalStatus.IN_PROGRESS,
        LocalDateTime.now(), null, 100, null, null, null, null
    );

    when(rentalRepository.findAllRentals(anyInt(), anyInt())).thenReturn(List.of(rental));
    when(rentalMapper.mapRentalToResponseDto(rental)).thenReturn(dto);

    Collection<RentalResponseDto> result = rentalService.findAllRentals(0, 10);

    assertNotNull(result);
    assertEquals(1, result.size());
  }
}