package ru.gigaden.electric_scooter_rental.repository;

import ru.gigaden.electric_scooter_rental.entity.Role;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

/**
 * Репозиторий для управления ролями пользователей
 */
public interface RoleRepository {

    Role saveRole(Role role);

    Role updateRole(Role role);

    Collection<Role> findAll();

    Optional<Role> findRoleByName(String name);

    Optional<Role> findRoleById(UUID id);

    void deleteRole(Role role);
}