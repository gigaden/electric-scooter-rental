package ru.gigaden.electric_scooter_rental.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.gigaden.electric_scooter_rental.dto.subscription.UserSubscriptionCreateDto;
import ru.gigaden.electric_scooter_rental.dto.subscription.UserSubscriptionResponseDto;
import ru.gigaden.electric_scooter_rental.service.UserSubscriptionService;

import java.util.UUID;

@RestController
@RequestMapping("/subscriptions")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Подписки", description = "Контроллер для управления подписками пользователей")
public class UserSubscriptionController {

    private final UserSubscriptionService subscriptionService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Создание подписки", description = "Активация подписки для пользователя")
    public UserSubscriptionResponseDto createSubscription(@Valid @RequestBody UserSubscriptionCreateDto dto) {

        log.info("Создаём подписку для пользователя {}", dto.userId());
        return subscriptionService.createSubscription(dto);
    }

    @GetMapping("/{subscriptionId}")
    @Operation(summary = "Получение подписки", description = "Получение информации о подписке по её id")
    public UserSubscriptionResponseDto getSubscription(@PathVariable UUID subscriptionId) {

        log.info("Получаем подписку с id = {}", subscriptionId);
        return subscriptionService.findSubscriptionById(subscriptionId);
    }
}