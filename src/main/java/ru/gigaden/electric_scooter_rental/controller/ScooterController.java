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
import ru.gigaden.electric_scooter_rental.dto.scooter.ScooterCreateDto;
import ru.gigaden.electric_scooter_rental.dto.scooter.ScooterResponseDto;
import ru.gigaden.electric_scooter_rental.dto.scooter.ScooterUpdateDto;
import ru.gigaden.electric_scooter_rental.entity.ScooterSortField;
import ru.gigaden.electric_scooter_rental.service.ScooterService;

import java.util.Collection;
import java.util.UUID;

/**
 * Контроллер обрабатывает основные эндпоинты самокатов
 */
@RestController
@RequestMapping("/scooters")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Самокаты", description = "Контроллер для управления самокатами")
public class ScooterController {

    private final ScooterService scooterService;

    /**
     * Создаём новый самокат
     */
    @PostMapping
    @Operation(summary = "Добавление самоката", description = "Добавление нового самоката в БД")
    public ResponseEntity<ScooterResponseDto> addScooter(@Valid @RequestBody ScooterCreateDto dto) {
        log.info("Создаём новый самокат {}", dto);
        ScooterResponseDto response = scooterService.addScooter(dto);

        return ResponseEntity.ok(response);
    }

    /**
     * Получаем самокат по его id
     */
    @GetMapping("/{scooterId}")
    @Operation(summary = "Получение самоката", description = "Получение самоката по его id")
    public ResponseEntity<ScooterResponseDto> getScooter(@PathVariable UUID scooterId) {
        log.info("Получаем самокат с id = {}", scooterId);
        ScooterResponseDto response = scooterService.findScooterById(scooterId);

        return ResponseEntity.ok(response);
    }

    /**
     * Получаем все самокаты
     */
    @GetMapping
    @Operation(summary = "Получение самокатов", description = "Получение самокатов с пагинацией и сортировкой")
    public ResponseEntity<Collection<ScooterResponseDto>> findAllScooters(@RequestParam(defaultValue = "0") int page,
                                                                          @RequestParam(defaultValue = "10") int size,
                                                                          @RequestParam(defaultValue = "BATTERY") String sort) {
        log.info("Получаем самокаты page={}, size={}, sort={}", page, size, sort);
        ScooterSortField sortField = ScooterSortField.fromString(sort);
        Collection<ScooterResponseDto> response = scooterService.findAll(page, size, sortField);

        return ResponseEntity.ok(response);
    }

    /**
     * Обновляем самокат
     */
    @PutMapping("/{scooterId}")
    @Operation(summary = "Обновление самоката", description = "Обновление самоката по его id")
    public ResponseEntity<ScooterResponseDto> updateScooterById(@PathVariable(name = "scooterId") UUID scooterId,
                                                                @Valid @RequestBody ScooterUpdateDto dto) {
        log.info("Обновляем самокат с id {}", scooterId);
        ScooterResponseDto response = scooterService.updateScooterById(scooterId, dto);

        return ResponseEntity.ok(response);
    }

    /**
     * Удаляем самокат
     */
    @DeleteMapping("/{scooterId}")
    @Operation(summary = "Удаление самоката", description = "Удаление самоката по его id")
    public ResponseEntity<String> deleteScooterById(@PathVariable(name = "scooterId") UUID scooterId) {
        log.info("Удаляем самокат с id {}", scooterId);
        scooterService.deleteScooterById(scooterId);

        return ResponseEntity.ok("Самокат удалён");
    }

}
