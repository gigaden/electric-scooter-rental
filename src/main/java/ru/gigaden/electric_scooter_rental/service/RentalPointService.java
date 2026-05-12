package ru.gigaden.electric_scooter_rental.service;

import ru.gigaden.electric_scooter_rental.dto.point.RentalPointCreateDto;
import ru.gigaden.electric_scooter_rental.dto.point.RentalPointResponseDto;
import ru.gigaden.electric_scooter_rental.dto.point.RentalPointUpdateDto;
import ru.gigaden.electric_scooter_rental.entity.RentalPoint;

import java.util.Collection;
import java.util.UUID;

/**
 * Интерфейс для сервиса точек проката
 */
public interface RentalPointService {

  RentalPointResponseDto addRentalPoint(RentalPointCreateDto dto);

  RentalPointResponseDto findRentalPointById(UUID id);

  RentalPoint findRowRentalPointOrThrow(UUID id);

  Collection<RentalPointResponseDto> findAllRentalPoints(int page, int size);

  RentalPointResponseDto updateRentalPoint(UUID id, RentalPointUpdateDto dto);

  void deleteRentalPointById(UUID id);

  Collection<RentalPointResponseDto> findRentalPointsByRadius(double latitude, double longitude, double radiusKm, int page, int size);
}
