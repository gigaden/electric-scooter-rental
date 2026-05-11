package ru.gigaden.electric_scooter_rental.service;

import ru.gigaden.electric_scooter_rental.dto.role.RoleCreateDto;
import ru.gigaden.electric_scooter_rental.dto.role.RoleResponseDto;
import ru.gigaden.electric_scooter_rental.dto.role.RoleUpdateDto;
import ru.gigaden.electric_scooter_rental.entity.Role;

import java.util.Collection;
import java.util.UUID;

/**
 * Интерфейс для сервиса ролей пользователей
 */
public interface RoleService {

  RoleResponseDto createRole(RoleCreateDto dto);

  Collection<RoleResponseDto> findAllRoles();

  Role findRowRoleByNameOrThrow(String name);

  Role findRowRoleByIdOrThrow(UUID id);

  RoleResponseDto updateRole(UUID roleId, RoleUpdateDto dto);

  void deleteRoleById(UUID roleId);
}
