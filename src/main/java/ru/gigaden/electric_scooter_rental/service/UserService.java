package ru.gigaden.electric_scooter_rental.service;

import ru.gigaden.electric_scooter_rental.dto.UserCreateDto;
import ru.gigaden.electric_scooter_rental.dto.UserResponseDto;
import ru.gigaden.electric_scooter_rental.dto.UserUpdateDto;
import ru.gigaden.electric_scooter_rental.entity.UserSortField;

import java.util.Collection;
import java.util.UUID;

/**
 * Интерфейс для сервиса пользователей
 */
public interface UserService {

    UserResponseDto addUser(UserCreateDto dto);

    UserResponseDto findUserById(UUID id);

    Collection<UserResponseDto> findAll(int page, int size, UserSortField sort);

    UserResponseDto updateUserById(UUID userId, UserUpdateDto dto);

    void deleteUserById(UUID id);
}
