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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.gigaden.electric_scooter_rental.dto.subscription.UserSubscriptionCreateDto;
import ru.gigaden.electric_scooter_rental.dto.subscription.UserSubscriptionResponseDto;
import ru.gigaden.electric_scooter_rental.service.UserSubscriptionService;

import java.util.UUID;

/**
 * Управляет подписками пользователей
 */
@RestController
@RequestMapping("/subscriptions")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Подписки", description = "Контроллер для управления подписками пользователей")
public class UserSubscriptionController {

  private final UserSubscriptionService subscriptionService;

  /**
   * Создаёт подписку
   */
  @PostMapping
  @PreAuthorize("@securityUtil.isOwner(#dto.userId) or hasRole('ADMIN')")
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Создание подписки", description = "Активация подписки для пользователя")
  public UserSubscriptionResponseDto createSubscription(@Valid @RequestBody UserSubscriptionCreateDto dto) {

    log.info("Создаём подписку для пользователя {}", dto.userId());
    return subscriptionService.createSubscription(dto);
  }

  /**
   * Получает подписку
   */
  @GetMapping("/{subscriptionId}")
  @PreAuthorize("authenticated()")
  @Operation(summary = "Получение подписки", description = "Получение информации о подписке по её id")
  public UserSubscriptionResponseDto getSubscription(@PathVariable UUID subscriptionId) {

    log.debug("Получаем подписку с id = {}", subscriptionId);
    return subscriptionService.findSubscriptionById(subscriptionId);
  }
}