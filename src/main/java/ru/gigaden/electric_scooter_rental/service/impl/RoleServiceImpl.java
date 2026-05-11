package ru.gigaden.electric_scooter_rental.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.gigaden.electric_scooter_rental.dto.role.RoleCreateDto;
import ru.gigaden.electric_scooter_rental.dto.role.RoleResponseDto;
import ru.gigaden.electric_scooter_rental.dto.role.RoleUpdateDto;
import ru.gigaden.electric_scooter_rental.entity.Role;
import ru.gigaden.electric_scooter_rental.exception.RoleNotFoundException;
import ru.gigaden.electric_scooter_rental.mapper.RoleMapper;
import ru.gigaden.electric_scooter_rental.repository.RoleRepository;
import ru.gigaden.electric_scooter_rental.service.RoleService;

import java.util.Collection;
import java.util.UUID;

/**
 * Реализация сервиса для ролей
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RoleServiceImpl implements RoleService {

  private final RoleRepository roleRepository;
  private final RoleMapper roleMapper;

  /**
   * Создание новой роли
   *
   * @param dto - dto роли
   * @return - dto с новой ролью
   */
  @Transactional
  @Override
  public RoleResponseDto createRole(RoleCreateDto dto) {

    Role role = roleRepository.saveRole(roleMapper.mapCreateDtoToRole(dto));
    RoleResponseDto response = roleMapper.mapRoleToResponseDto(role);
    log.info("Добавлена новая роль {}", response);

    return response;
  }

  /**
   * Находим все возможные роли пользователей
   */
  @Override
  public Collection<RoleResponseDto> findAllRoles() {

    Collection<RoleResponseDto> response = roleRepository.findAll().stream()
        .map(roleMapper::mapRoleToResponseDto)
        .toList();
    log.debug("Получили список ролей пользователей в количестве {}", response.size());

    return response;
  }

  /**
   * Ищет роль по имени
   *
   * @param name - имя роли
   * @return - объект роли
   * @throws RoleNotFoundException - если роль не найдена
   */
  @Override
  public Role findRowRoleByNameOrThrow(String name) {

    Role role = roleRepository.findRoleByName(name)
        .orElseThrow(() -> new RoleNotFoundException("Роль не найдена"));
    log.debug("Найдена роль {}", name);

    return role;
  }

  @Override
  public Role findRowRoleByIdOrThrow(UUID id) {

    Role role = roleRepository.findRoleById(id)
        .orElseThrow(() -> new RoleNotFoundException("Роль не найдена"));
    log.debug("Получена роль с id = {}", id);

    return role;
  }

  @Transactional
  @Override
  public RoleResponseDto updateRole(UUID roleId, RoleUpdateDto dto) {

    Role oldRole = findRowRoleByIdOrThrow(roleId);

    if (dto.name() != null) {
      oldRole.setName(dto.name());
    }

    Role updatedRole = roleRepository.updateRole(oldRole);
    RoleResponseDto response = roleMapper.mapRoleToResponseDto(updatedRole);
    log.info("Роль с id = {} обновлена", roleId);

    return response;
  }

  @Transactional
  @Override
  public void deleteRoleById(UUID roleId) {

    Role role = findRowRoleByIdOrThrow(roleId);
    roleRepository.deleteRole(role);

    log.info("Роль с id = {} удалена", roleId);
  }
}
