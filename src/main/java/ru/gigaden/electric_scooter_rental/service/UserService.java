package ru.gigaden.electric_scooter_rental.service;

import ru.gigaden.electric_scooter_rental.dto.UserCreateDto;
import ru.gigaden.electric_scooter_rental.dto.UserResponseDto;

import java.util.UUID;

/**
 * Интерфейс для сервиса пользователей
 */
public interface UserService {

    UserResponseDto addUser(UserCreateDto dto);

    UserResponseDto findUserById(UUID id);
}
