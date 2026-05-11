package ru.gigaden.electric_scooter_rental.service;

import ru.gigaden.electric_scooter_rental.dto.scooter.ScooterCreateDto;
import ru.gigaden.electric_scooter_rental.dto.scooter.ScooterResponseDto;
import ru.gigaden.electric_scooter_rental.dto.scooter.ScooterUpdateDto;
import ru.gigaden.electric_scooter_rental.entity.ScooterSortField;

import java.util.Collection;
import java.util.UUID;

/**
 * Интерфейс для сервиса самокатов
 */
public interface ScooterService {

  ScooterResponseDto addScooter(ScooterCreateDto dto);

  ScooterResponseDto findScooterById(UUID id);

  Collection<ScooterResponseDto> findAll(int page, int size, ScooterSortField sort);

  ScooterResponseDto updateScooterById(UUID scooterId, ScooterUpdateDto dto);

  void deleteScooterById(UUID id);

  boolean checkScooterIsExist(UUID scooterId);
}
