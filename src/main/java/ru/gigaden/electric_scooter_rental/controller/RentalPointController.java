package ru.gigaden.electric_scooter_rental.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.gigaden.electric_scooter_rental.dto.point.RentalPointCreateDto;
import ru.gigaden.electric_scooter_rental.dto.point.RentalPointResponseDto;
import ru.gigaden.electric_scooter_rental.dto.point.RentalPointUpdateDto;
import ru.gigaden.electric_scooter_rental.entity.RentalPointSortField;
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
    @Operation(summary = "Добавление точки аренды", description = "Добавление новой точки аренды в БД")
    public ResponseEntity<RentalPointResponseDto> addRentalPoint(@Valid @RequestBody RentalPointCreateDto dto) {
        log.info("Создаём новую точку аренды {}", dto);
        RentalPointResponseDto response = pointService.addRentalPoint(dto);

        return ResponseEntity.ok(response);
    }

    /**
     * Получаем точку аренды по её id
     */
    @GetMapping("/{pointId}")
    @Operation(summary = "Получение точки аренды", description = "Получение точки аренды по её id")
    public ResponseEntity<RentalPointResponseDto> getRentalPoint(@PathVariable UUID pointId) {
        log.info("Получаем точку аренды с id = {}", pointId);
        RentalPointResponseDto response = pointService.findRentalPointById(pointId);

        return ResponseEntity.ok(response);
    }

    /**
     * Получаем все точки аренды
     */
    @GetMapping
    @Operation(summary = "Получение точек аренды", description = "Получение точек аренды с пагинацией и сортировкой")
    public ResponseEntity<Collection<RentalPointResponseDto>> findAllRentalPoints(@RequestParam(defaultValue = "0") int page,
                                                                                  @RequestParam(defaultValue = "10") int size,
                                                                                  @RequestParam(defaultValue = "TOTAL_SCOOTERS") RentalPointSortField sort) {
        log.info("Получаем точки аренды page={}, size={}, sort={}", page, size, sort);
        Collection<RentalPointResponseDto> response = pointService.findAllRentalPoints(page, size, sort);

        return ResponseEntity.ok(response);
    }

    /**
     * Обновляем точку аренды
     */
    @PutMapping("/{pointId}")
    @Operation(summary = "Обновление точки аренды", description = "Обновление точки аренды по её id")
    public ResponseEntity<RentalPointResponseDto> updateRentalPointById(@PathVariable(name = "pointId") UUID pointId,
                                                                        @Valid @RequestBody RentalPointUpdateDto dto) {
        log.info("Обновляем точку аренды с id {}", pointId);
        RentalPointResponseDto response = pointService.updateRentalPoint(pointId, dto);

        return ResponseEntity.ok(response);
    }

    /**
     * Удаляем точку аренды
     */
    @DeleteMapping("/{pointId}")
    @Operation(summary = "Удаление точки аренды", description = "Удаление точки аренды по её id")
    public ResponseEntity<String> deleteRentalPointById(@PathVariable(name = "pointId") UUID pointId) {
        log.info("Удаляем точку аренды с id {}", pointId);
        pointService.deleteRentalPointById(pointId);

        return ResponseEntity.ok("Точка аренды удалена");
    }

}
