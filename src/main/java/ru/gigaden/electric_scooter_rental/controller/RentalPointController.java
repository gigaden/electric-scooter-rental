package ru.gigaden.electric_scooter_rental.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.gigaden.electric_scooter_rental.dto.point.RentalPointCreateDto;
import ru.gigaden.electric_scooter_rental.dto.point.RentalPointResponseDto;
import ru.gigaden.electric_scooter_rental.dto.point.RentalPointUpdateDto;
import ru.gigaden.electric_scooter_rental.service.RentalPointService;

import java.util.Collection;
import java.util.UUID;

/**
 * Контроллер обрабатывает основные эндпоинты точек аренды
 */
@RestController
@RequestMapping("/points")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Точки аренды", description = "Контроллер для управления точками аренды")
public class RentalPointController {

  private final RentalPointService pointService;

  /**
   * Создаём новую точку аренды
   */
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @PreAuthorize("hasRole('ADMIN')")
  @Operation(summary = "Добавление точки аренды", description = "Добавление новой точки аренды в БД")
  public RentalPointResponseDto addRentalPoint(@Valid @RequestBody RentalPointCreateDto dto) {

    log.info("Создаём новую точку аренды {}", dto);

    return pointService.addRentalPoint(dto);
  }

  /**
   * Получаем точку аренды по её id
   */
  @GetMapping("/{pointId}")
  @Operation(summary = "Получение точки аренды", description = "Получение точки аренды по её id")
  public RentalPointResponseDto getRentalPoint(@PathVariable UUID pointId) {

    log.debug("Получаем точку аренды с id = {}", pointId);

    return pointService.findRentalPointById(pointId);
  }

  /**
   * Получаем все точки аренды
   */
  @GetMapping
  @Operation(summary = "Получение точек аренды", description = "Получение точек аренды с пагинацией и сортировкой")
  public Collection<RentalPointResponseDto> findAllRentalPoints(@RequestParam(defaultValue = "0") int page,
                                                                @RequestParam(defaultValue = "10") int size) {

    log.debug("Получаем точки аренды page={}, size={}", page, size);

    return pointService.findAllRentalPoints(page, size);
  }

  /**
   * Обновляем точку аренды
   */
  @PutMapping("/{pointId}")
  @PreAuthorize("hasRole('ADMIN')")
  @Operation(summary = "Обновление точки аренды", description = "Обновление точки аренды по её id")
  public RentalPointResponseDto updateRentalPointById(@PathVariable(name = "pointId") UUID pointId,
                                                      @Valid @RequestBody RentalPointUpdateDto dto) {
    log.info("Обновляем точку аренды с id {}", pointId);

    return pointService.updateRentalPoint(pointId, dto);
  }

  /**
   * Удаляем точку аренды
   */
  @DeleteMapping("/{pointId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PreAuthorize("hasRole('ADMIN')")
  @Operation(summary = "Удаление точки аренды", description = "Удаление точки аренды по её id")
  public void deleteRentalPointById(@PathVariable(name = "pointId") UUID pointId) {

    log.info("Удаляем точку аренды с id {}", pointId);

    pointService.deleteRentalPointById(pointId);
  }

  /**
   * Ищет точки аренды по радиусу.
   */
  @GetMapping("/search")
  @Operation(summary = "Поиск точек аренды по радиусу", description = "Поиск точек аренды в радиусе от заданных координат")
  public Collection<RentalPointResponseDto> findRentalPointsByRadius(@RequestParam double latitude,
                                                                     @RequestParam double longitude,
                                                                     @RequestParam(defaultValue = "5.0") double radiusKm,
                                                                     @RequestParam(defaultValue = "0") int page,
                                                                     @RequestParam(defaultValue = "10") int size) {

    log.debug("Поиск точек аренды в радиусе {} км от ({}, {})", radiusKm, latitude, longitude);

    return pointService.findRentalPointsByRadius(latitude, longitude, radiusKm, page, size);
  }

}
