package ru.gigaden.electric_scooter_rental.repository;

import ru.gigaden.electric_scooter_rental.entity.Role;

import java.util.Optional;

/**
 * Репозиторий для управления ролями пользователей
 * */
public interface RoleRepository {

    Optional<Role> findRoleByName(String name);
}