package ru.gigaden.electric_scooter_rental.service.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.gigaden.electric_scooter_rental.dto.scooter.ScooterCreateDto;
import ru.gigaden.electric_scooter_rental.dto.scooter.ScooterResponseDto;
import ru.gigaden.electric_scooter_rental.dto.scooter.ScooterUpdateDto;
import ru.gigaden.electric_scooter_rental.entity.RentalPoint;
import ru.gigaden.electric_scooter_rental.entity.Scooter;
import ru.gigaden.electric_scooter_rental.entity.ScooterSortField;
import ru.gigaden.electric_scooter_rental.entity.ScooterStatus;
import ru.gigaden.electric_scooter_rental.exception.RentalPointNotFoundException;
import ru.gigaden.electric_scooter_rental.exception.ScooterNotFoundException;
import ru.gigaden.electric_scooter_rental.mapper.ScooterMapper;
import ru.gigaden.electric_scooter_rental.repository.ScooterRepository;
import ru.gigaden.electric_scooter_rental.service.RentalPointService;

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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тесты сервиса самокатов")
class ScooterServiceImplTest {

  @InjectMocks
  private ScooterServiceImpl scooterService;

  @Mock
  private ScooterMapper scooterMapper;

  @Mock
  private ScooterRepository scooterRepository;

  @Mock
  private RentalPointService rentalPointService;

  private static final UUID POINT_ID = UUID.randomUUID();
  private static final String VALID_MODEL = "Nimbus2000";
  private static final String VALID_DESCRIPTION_CREATE = "Качественный электросамокат для городской аренды с мощным аккумулятором";
  private static final String VALID_DESCRIPTION_UPDATE = "Качественный электросамокат для городской аренды с мощным аккумулятором и улучшенной подвеской";

  @Test
  @DisplayName("Добавление нового самоката")
  void addScooterShouldBePositive() {
    ScooterCreateDto createDto = ScooterCreateDto.builder()
        .rentalPointId(POINT_ID)
        .model(VALID_MODEL)
        .description(VALID_DESCRIPTION_CREATE)
        .batteryPower(100)
        .mileage(0)
        .build();

    RentalPoint rentalPoint = RentalPoint.builder()
        .id(POINT_ID)
        .latitude(56.3269)
        .longitude(44.0059)
        .address("Test Address")
        .description("Test Description")
        .build();

    Scooter scooterBeforeSave = Scooter.builder()
        .rentalPoint(rentalPoint)
        .latitude(56.3269)
        .longitude(44.0059)
        .model(VALID_MODEL)
        .description(VALID_DESCRIPTION_CREATE)
        .status(ScooterStatus.AVAILABLE)
        .batteryPower(100)
        .mileage(0)
        .build();

    UUID savedId = UUID.randomUUID();
    Scooter savedScooter = Scooter.builder()
        .id(savedId)
        .rentalPoint(rentalPoint)
        .latitude(56.3269)
        .longitude(44.0059)
        .model(VALID_MODEL)
        .description(VALID_DESCRIPTION_CREATE)
        .status(ScooterStatus.AVAILABLE)
        .batteryPower(100)
        .mileage(0)
        .updatedOn(LocalDateTime.now())
        .build();

    ScooterResponseDto responseDto = new ScooterResponseDto(
        savedId, POINT_ID, 56.3269, 44.0059, VALID_MODEL,
        VALID_DESCRIPTION_CREATE, ScooterStatus.AVAILABLE, 100, 0, savedScooter.getUpdatedOn()
    );

    when(rentalPointService.findRowRentalPointOrThrow(POINT_ID)).thenReturn(rentalPoint);
    when(scooterRepository.saveScooter(any(Scooter.class))).thenReturn(savedScooter);
    when(scooterMapper.mapScooterToResponseDto(savedScooter)).thenReturn(responseDto);

    ScooterResponseDto result = scooterService.addScooter(createDto);

    assertNotNull(result);
    assertEquals(savedId, result.id());
    assertEquals(VALID_MODEL, result.model());
    verify(rentalPointService).findRowRentalPointOrThrow(POINT_ID);
    verify(scooterRepository).saveScooter(any(Scooter.class));
  }

  @Test
  @DisplayName("Добавление самоката с несуществующей точкой аренды")
  void addScooterShouldThrowWhenRentalPointNotFound() {
    ScooterCreateDto createDto = ScooterCreateDto.builder()
        .rentalPointId(POINT_ID)
        .model(VALID_MODEL)
        .description(VALID_DESCRIPTION_CREATE)
        .batteryPower(100)
        .mileage(0)
        .build();

    when(rentalPointService.findRowRentalPointOrThrow(POINT_ID))
        .thenThrow(new RentalPointNotFoundException("Точка аренды не найдена"));

    assertThrows(RentalPointNotFoundException.class, () -> scooterService.addScooter(createDto));
    verify(scooterRepository, never()).saveScooter(any());
  }

  @Test
  @DisplayName("Получение самоката по id")
  void findScooterByIdShouldBePositive() {
    UUID scooterId = UUID.randomUUID();
    RentalPoint rentalPoint = RentalPoint.builder().id(POINT_ID).build();

    Scooter scooter = Scooter.builder()
        .id(scooterId)
        .rentalPoint(rentalPoint)
        .model(VALID_MODEL)
        .description(VALID_DESCRIPTION_CREATE)
        .status(ScooterStatus.AVAILABLE)
        .batteryPower(100)
        .mileage(0)
        .build();

    ScooterResponseDto responseDto = new ScooterResponseDto(
        scooterId, POINT_ID, 56.3269, 44.0059, VALID_MODEL,
        VALID_DESCRIPTION_CREATE, ScooterStatus.AVAILABLE, 100, 0, LocalDateTime.now()
    );

    when(scooterRepository.findScooterById(scooterId)).thenReturn(Optional.of(scooter));
    when(scooterMapper.mapScooterToResponseDto(scooter)).thenReturn(responseDto);

    ScooterResponseDto result = scooterService.findScooterById(scooterId);

    assertNotNull(result);
    assertEquals(scooterId, result.id());
    verify(scooterRepository).findScooterById(scooterId);
  }

  @Test
  @DisplayName("Поиск самоката по несуществующему ID")
  void findScooterByIdShouldThrowWhenNotFound() {
    UUID scooterId = UUID.randomUUID();
    when(scooterRepository.findScooterById(scooterId)).thenReturn(Optional.empty());

    assertThrows(ScooterNotFoundException.class, () -> scooterService.findScooterById(scooterId));
    verify(scooterMapper, never()).mapScooterToResponseDto(any());
  }

  @Test
  @DisplayName("Получение всех самокатов с пагинацией")
  void findAllShouldBePositive() {
    RentalPoint rentalPoint = RentalPoint.builder().id(POINT_ID).build();
    Scooter scooter = Scooter.builder()
        .id(UUID.randomUUID())
        .rentalPoint(rentalPoint)
        .model(VALID_MODEL)
        .description(VALID_DESCRIPTION_CREATE)
        .status(ScooterStatus.AVAILABLE)
        .batteryPower(100)
        .mileage(0)
        .build();

    ScooterResponseDto dto = new ScooterResponseDto(
        scooter.getId(), POINT_ID, 56.3269, 44.0059, VALID_MODEL,
        VALID_DESCRIPTION_CREATE, ScooterStatus.AVAILABLE, 100, 0, LocalDateTime.now()
    );

    when(scooterRepository.findAllScooters(anyInt(), anyInt(), anyString()))
        .thenReturn(List.of(scooter));
    when(scooterMapper.mapScooterToResponseDto(scooter)).thenReturn(dto);

    Collection<ScooterResponseDto> result = scooterService.findAll(0, 10, ScooterSortField.BATTERY);

    assertNotNull(result);
    assertEquals(1, result.size());
    verify(scooterRepository).findAllScooters(0, 10, "batteryPower");
  }

  @Test
  @DisplayName("Обновление самоката")
  void updateScooterByIdShouldBePositive() {
    UUID scooterId = UUID.randomUUID();
    RentalPoint rentalPoint = RentalPoint.builder().id(POINT_ID).latitude(56.33).longitude(44.01).build();

    Scooter existingScooter = Scooter.builder()
        .id(scooterId)
        .rentalPoint(rentalPoint)
        .model("OldModel")
        .description("Old description for testing purposes only")
        .status(ScooterStatus.AVAILABLE)
        .batteryPower(50)
        .mileage(100)
        .build();

    ScooterUpdateDto updateDto = new ScooterUpdateDto(
        POINT_ID, 56.33, 44.01, "NewModel", VALID_DESCRIPTION_UPDATE,
        ScooterStatus.MAINTENANCE, 80, 150
    );

    Scooter updatedScooter = Scooter.builder()
        .id(scooterId)
        .rentalPoint(rentalPoint)
        .model("NewModel")
        .description(VALID_DESCRIPTION_UPDATE)
        .status(ScooterStatus.MAINTENANCE)
        .batteryPower(80)
        .mileage(150)
        .build();

    ScooterResponseDto responseDto = new ScooterResponseDto(
        scooterId, POINT_ID, 56.33, 44.01, "NewModel",
        VALID_DESCRIPTION_UPDATE, ScooterStatus.MAINTENANCE, 80, 150, LocalDateTime.now()
    );

    when(scooterRepository.findScooterById(scooterId)).thenReturn(Optional.of(existingScooter));
    when(rentalPointService.findRowRentalPointOrThrow(POINT_ID)).thenReturn(rentalPoint);
    when(scooterRepository.updateScooter(any(Scooter.class))).thenReturn(updatedScooter);
    when(scooterMapper.mapScooterToResponseDto(updatedScooter)).thenReturn(responseDto);

    ScooterResponseDto result = scooterService.updateScooterById(scooterId, updateDto);

    assertNotNull(result);
    assertEquals("NewModel", result.model());
    assertEquals(ScooterStatus.MAINTENANCE, result.status());
    verify(scooterRepository).updateScooter(any(Scooter.class));
  }

  @Test
  @DisplayName("Обновление самоката с несуществующей точкой аренды")
  void updateScooterShouldThrowWhenRentalPointNotFound() {
    UUID scooterId = UUID.randomUUID();
    Scooter existingScooter = Scooter.builder().id(scooterId).build();
    ScooterUpdateDto updateDto = new ScooterUpdateDto(
        POINT_ID, 56.33, 44.01, VALID_MODEL, VALID_DESCRIPTION_UPDATE,
        ScooterStatus.AVAILABLE, 100, 0
    );

    when(scooterRepository.findScooterById(scooterId)).thenReturn(Optional.of(existingScooter));
    when(rentalPointService.findRowRentalPointOrThrow(POINT_ID))
        .thenThrow(new RentalPointNotFoundException("Точка аренды не найдена"));

    assertThrows(RentalPointNotFoundException.class,
        () -> scooterService.updateScooterById(scooterId, updateDto));
  }

  @Test
  @DisplayName("Обновление несуществующего самоката")
  void updateScooterShouldThrowWhenScooterNotFound() {
    UUID scooterId = UUID.randomUUID();
    ScooterUpdateDto updateDto = new ScooterUpdateDto(
        POINT_ID, 56.33, 44.01, VALID_MODEL, VALID_DESCRIPTION_UPDATE,
        ScooterStatus.AVAILABLE, 100, 0
    );

    when(scooterRepository.findScooterById(scooterId)).thenReturn(Optional.empty());

    assertThrows(ScooterNotFoundException.class,
        () -> scooterService.updateScooterById(scooterId, updateDto));
  }

  @Test
  @DisplayName("Удаление самоката")
  void deleteScooterByIdShouldBePositive() {
    UUID scooterId = UUID.randomUUID();
    Scooter scooter = Scooter.builder().id(scooterId).build();

    when(scooterRepository.findScooterById(scooterId)).thenReturn(Optional.of(scooter));

    scooterService.deleteScooterById(scooterId);

    verify(scooterRepository).deleteScooterByEntity(scooter);
  }

  @Test
  @DisplayName("Удаление несуществующего самоката")
  void deleteScooterByIdShouldThrowWhenNotFound() {
    UUID scooterId = UUID.randomUUID();
    when(scooterRepository.findScooterById(scooterId)).thenReturn(Optional.empty());

    assertThrows(ScooterNotFoundException.class, () -> scooterService.deleteScooterById(scooterId));
  }
}