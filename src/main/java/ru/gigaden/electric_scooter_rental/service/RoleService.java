package ru.gigaden.electric_scooter_rental.service;

import ru.gigaden.electric_scooter_rental.entity.Role;

/**
 * Интерфейс для сервиса ролей пользователей
 * */
public interface RoleService {

    Role findRowRoleByNameOrThrow(String name);
}
