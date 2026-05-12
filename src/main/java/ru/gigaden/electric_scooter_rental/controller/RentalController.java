package ru.gigaden.electric_scooter_rental.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.gigaden.electric_scooter_rental.dto.rental.RentalCreateDto;
import ru.gigaden.electric_scooter_rental.dto.rental.RentalResponseDto;
import ru.gigaden.electric_scooter_rental.service.RentalService;

import java.util.Collection;
import java.util.UUID;

/**
 * Контроллер обрабатывает основные эндпоинты точек аренды.
 */
@RestController
@RequestMapping("/rentals")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Аренды", description = "Контроллер для управления арендами")
public class RentalController {

  private final RentalService rentalService;

  /**
   * Создаём новую аренду.
   */
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @PreAuthorize("isAuthenticated()")
  @Operation(summary = "Добавление аренды", description = "Добавление новой аренды в БД")
  public RentalResponseDto addRental(@Valid @RequestBody RentalCreateDto dto) {

    log.info("Создаём новую аренду ");

    return rentalService.createRental(dto);
  }

  /**
   * Получаем аренду по её id.
   */
  @GetMapping("/{rentalId}")
  @PreAuthorize("isAuthenticated()")
  @Operation(summary = "Получение аренды", description = "Получение аренды по её id")
  public RentalResponseDto getRentalById(@PathVariable UUID rentalId) {

    log.debug("Получаем аренду с id = {}", rentalId);

    return rentalService.findRentalById(rentalId);
  }

  /**
   * Получаем все аренды.
   */
  @GetMapping
  @PreAuthorize("hasRole('ADMIN')")
  @Operation(summary = "Получение аренд", description = "Получение всех аренд с пагинацией и сортировкой по id")
  public Collection<RentalResponseDto> findAllRental(@RequestParam(defaultValue = "0") int page,
                                                     @RequestParam(defaultValue = "10") int size) {

    log.debug("Получаем все аренды page={}, size={}", page, size);

    return rentalService.findAllRentals(page, size);
  }

  /**
   * Завершает аренду.
   */
  @PostMapping("/{rentalId}/complete")
  @PreAuthorize("isAuthenticated()")
  @Operation(summary = "Завершение аренды", description = "Завершение аренды по её id")
  public RentalResponseDto completeRentalById(@PathVariable(name = "rentalId") UUID rentalId) {

    log.info("Завершаем аренду с id {}", rentalId);

    return rentalService.completeRental(rentalId);
  }

  /**
   * Получаем историю аренды пользователя.
   */
  @GetMapping("/user/{userId}")
  @PreAuthorize("@securityUtil.isOwner(#userId) or hasRole('ADMIN')")
  @Operation(summary = "История аренды пользователя", description = "Получение истории аренд конкретного пользователя")
  public Collection<RentalResponseDto> getRentalHistoryByUser(@PathVariable UUID userId,
                                                              @RequestParam(defaultValue = "0") int page,
                                                              @RequestParam(defaultValue = "10") int size) {

    log.debug("Получаем историю аренды пользователя {} page={}, size={}", userId, page, size);

    return rentalService.findRentalsByUserId(userId, page, size);
  }

  /**
   * Получаем историю аренды самоката.
   */
  @GetMapping("/scooter/{scooterId}")
  @PreAuthorize("hasRole('ADMIN')")
  @Operation(summary = "История аренды самоката", description = "Получение завершённых аренд конкретного самоката (для админа)")
  public Collection<RentalResponseDto> getRentalHistoryByScooter(@PathVariable UUID scooterId,
                                                                 @RequestParam(defaultValue = "0") int page,
                                                                 @RequestParam(defaultValue = "10") int size) {

    log.debug("Получаем историю аренды самоката {} page={}, size={}", scooterId, page, size);

    return rentalService.findFinishedRentalsByScooterId(scooterId, page, size);
  }

}
