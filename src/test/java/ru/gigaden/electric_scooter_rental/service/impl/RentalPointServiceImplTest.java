package ru.gigaden.electric_scooter_rental.service.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.gigaden.electric_scooter_rental.dto.point.RentalPointCreateDto;
import ru.gigaden.electric_scooter_rental.dto.point.RentalPointResponseDto;
import ru.gigaden.electric_scooter_rental.dto.point.RentalPointUpdateDto;
import ru.gigaden.electric_scooter_rental.entity.RentalPoint;
import ru.gigaden.electric_scooter_rental.exception.RentalPointNotFoundException;
import ru.gigaden.electric_scooter_rental.mapper.RentalPointMapper;
import ru.gigaden.electric_scooter_rental.repository.RentalPointRepository;

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
@DisplayName("Тесты сервиса точек аренды")
class RentalPointServiceImplTest {

  @InjectMocks
  private RentalPointServiceImpl rentalPointService;

  @Mock
  private RentalPointMapper pointMapper;

  @Mock
  private RentalPointRepository rentalPointRepository;

  @Test
  @DisplayName("Добавление новой точки аренды")
  void addRentalPointShouldBePositive() {
    RentalPointCreateDto createDto = RentalPointCreateDto.builder()
        .address("Н.Новгород, Бурнаковская 103")
        .latitude(56.3269)
        .longitude(44.0059)
        .description("Описание точки аренды")
        .build();

    RentalPoint pointBeforeSave = RentalPoint.builder()
        .address("Н.Новгород, Бурнаковская 103")
        .latitude(56.3269)
        .longitude(44.0059)
        .description("Описание точки аренды")
        .build();

    UUID savedId = UUID.randomUUID();
    RentalPoint savedPoint = RentalPoint.builder()
        .id(savedId)
        .address("Н.Новгород, Бурнаковская 103")
        .latitude(56.3269)
        .longitude(44.0059)
        .description("Описание точки аренды")
        .addedOn(LocalDateTime.now())
        .updatedOn(LocalDateTime.now())
        .build();

    RentalPointResponseDto responseDto = new RentalPointResponseDto(
        savedPoint.getId(),
        savedPoint.getAddress(),
        savedPoint.getLatitude(),
        savedPoint.getLongitude(),
        savedPoint.getDescription(),
        savedPoint.getAddedOn(),
        savedPoint.getUpdatedOn(),
        List.of()
    );

    when(pointMapper.mapCreateToRentalPoint(createDto)).thenReturn(pointBeforeSave);
    when(rentalPointRepository.saveRentalPoint(pointBeforeSave)).thenReturn(savedPoint);
    when(pointMapper.mapRentalPointToResponse(savedPoint)).thenReturn(responseDto);

    RentalPointResponseDto result = rentalPointService.addRentalPoint(createDto);

    assertNotNull(result);
    assertEquals(savedId, result.id());
    assertEquals(createDto.address(), result.address());
    verify(rentalPointRepository).saveRentalPoint(pointBeforeSave);
  }

  @Test
  @DisplayName("Получение точки аренды по id")
  void findRentalPointByIdShouldBePositive() {
    UUID id = UUID.randomUUID();
    RentalPoint savedPoint = RentalPoint.builder()
        .id(id)
        .address("Н.Новгород, Бурнаковская 103")
        .latitude(56.3269)
        .longitude(44.0059)
        .description("Описание точки аренды")
        .addedOn(LocalDateTime.now())
        .updatedOn(LocalDateTime.now())
        .build();

    RentalPointResponseDto responseDto = new RentalPointResponseDto(
        savedPoint.getId(),
        savedPoint.getAddress(),
        savedPoint.getLatitude(),
        savedPoint.getLongitude(),
        savedPoint.getDescription(),
        savedPoint.getAddedOn(),
        savedPoint.getUpdatedOn(),
        List.of()
    );

    when(rentalPointRepository.findRentalPointById(id)).thenReturn(Optional.of(savedPoint));
    when(pointMapper.mapRentalPointToResponse(savedPoint)).thenReturn(responseDto);

    RentalPointResponseDto result = rentalPointService.findRentalPointById(id);

    assertNotNull(result);
    assertEquals(savedPoint.getAddress(), result.address());
    verify(rentalPointRepository).findRentalPointById(id);
  }

  @Test
  @DisplayName("Поиск точки аренды по несуществующему ID должен бросать исключение")
  void findRentalPointByIdShouldThrowWhenNotFound() {
    UUID id = UUID.randomUUID();
    when(rentalPointRepository.findRentalPointById(id)).thenReturn(Optional.empty());

    assertThrows(RentalPointNotFoundException.class, () -> rentalPointService.findRentalPointById(id));
    verify(pointMapper, never()).mapRentalPointToResponse(any());
  }

  @Test
  @DisplayName("Получение всех точек аренды с пагинацией")
  void findAllRentalPointsShouldBePositive() {
    RentalPoint point = RentalPoint.builder()
        .id(UUID.randomUUID())
        .address("Н.Новгород, Бурнаковская 103")
        .latitude(56.3269)
        .longitude(44.0059)
        .description("Описание точки аренды")
        .build();

    RentalPointResponseDto dto = new RentalPointResponseDto(
        point.getId(),
        point.getAddress(),
        point.getLatitude(),
        point.getLongitude(),
        point.getDescription(),
        point.getAddedOn(),
        point.getUpdatedOn(),
        List.of()
    );

    when(rentalPointRepository.findAllRentalPoints(anyInt(), anyInt()))
        .thenReturn(List.of(point));
    when(pointMapper.mapRentalPointToResponse(point)).thenReturn(dto);

    Collection<RentalPointResponseDto> result =
        rentalPointService.findAllRentalPoints(0, 10);

    assertNotNull(result);
    assertEquals(1, result.size());
    verify(rentalPointRepository).findAllRentalPoints(0, 10);
  }

  @Test
  @DisplayName("Обновление точки аренды")
  void updateRentalPointShouldBePositive() {
    UUID id = UUID.randomUUID();

    RentalPointUpdateDto updateDto = new RentalPointUpdateDto(
        "Н.Новгород, новая улица 1",
        56.3300,
        44.0100,
        "Обновлённое описание"
    );

    RentalPoint existingPoint = RentalPoint.builder()
        .id(id)
        .address("Старый адрес")
        .latitude(56.3200)
        .longitude(44.0000)
        .description("Старое описание")
        .build();

    RentalPoint updatedPoint = RentalPoint.builder()
        .id(id)
        .address("Н.Новгород, новая улица 1")
        .latitude(56.3300)
        .longitude(44.0100)
        .description("Обновлённое описание")
        .build();

    RentalPointResponseDto responseDto = new RentalPointResponseDto(
        id,
        updatedPoint.getAddress(),
        updatedPoint.getLatitude(),
        updatedPoint.getLongitude(),
        updatedPoint.getDescription(),
        LocalDateTime.now(),
        LocalDateTime.now(),
        List.of()
    );

    when(rentalPointRepository.findRentalPointById(id)).thenReturn(Optional.of(existingPoint));
    when(rentalPointRepository.updateRentalPoint(any())).thenReturn(updatedPoint);
    when(pointMapper.mapRentalPointToResponse(updatedPoint)).thenReturn(responseDto);

    RentalPointResponseDto result = rentalPointService.updateRentalPoint(id, updateDto);

    assertNotNull(result);
    assertEquals("Н.Новгород, новая улица 1", result.address());
    verify(rentalPointRepository).updateRentalPoint(any());
  }

  @Test
  @DisplayName("Обновление несуществующей точки аренды")
  void updateRentalPointShouldThrowWhenNotFound() {
    UUID id = UUID.randomUUID();
    RentalPointUpdateDto dto = new RentalPointUpdateDto(
        "Адрес",
        56.3269,
        44.0059,
        "Описание"
    );

    when(rentalPointRepository.findRentalPointById(id)).thenReturn(Optional.empty());

    assertThrows(RentalPointNotFoundException.class,
        () -> rentalPointService.updateRentalPoint(id, dto));
  }

  @Test
  @DisplayName("Удаление точки аренды")
  void deleteRentalPointByIdShouldBePositive() {
    UUID id = UUID.randomUUID();
    RentalPoint point = RentalPoint.builder().id(id).build();

    when(rentalPointRepository.findRentalPointById(id)).thenReturn(Optional.of(point));

    rentalPointService.deleteRentalPointById(id);

    verify(rentalPointRepository).deleteRentalPoint(point);
  }

  @Test
  @DisplayName("Удаление несуществующей точки аренды")
  void deleteRentalPointByIdShouldThrowWhenNotFound() {
    UUID id = UUID.randomUUID();
    when(rentalPointRepository.findRentalPointById(id)).thenReturn(Optional.empty());

    assertThrows(RentalPointNotFoundException.class,
        () -> rentalPointService.deleteRentalPointById(id));
  }
}