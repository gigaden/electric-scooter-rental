package ru.gigaden.electric_scooter_rental.service.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.gigaden.electric_scooter_rental.entity.Role;
import ru.gigaden.electric_scooter_rental.exception.RoleNotFoundException;
import ru.gigaden.electric_scooter_rental.repository.RoleRepository;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тесты сервиса ролей")
class RoleServiceImplTest {

  @InjectMocks
  private RoleServiceImpl roleService;

  @Mock
  private RoleRepository roleRepository;

  @Test
  @DisplayName("Поиск роли по её имени")
  void findRoleByNameShouldBePositive() {
    String roleName = "USER";
    UUID id = UUID.randomUUID();
    Role expectedRole = Role.builder()
        .id(id)
        .name(roleName)
        .build();

    when(roleRepository.findRoleByName(roleName)).thenReturn(Optional.of(expectedRole));

    Role result = roleService.findRowRoleByNameOrThrow(roleName);

    assertEquals(roleName, result.getName());
    verify(roleRepository).findRoleByName(roleName);

  }

  @Test
  @DisplayName("Роль не найдена - выброс исключения")
  void findRoleShouldThrowWhenRoleNameNotFound() {

    when(roleRepository.findRoleByName(any())).thenReturn(Optional.empty());

    assertThrows(RoleNotFoundException.class, () -> roleService.findRowRoleByNameOrThrow("GODFATHER"));
  }

  @Test
  @DisplayName("Поиск роли по её id")
  void findRoleByIdShouldBePositive() {
    String roleName = "USER";
    UUID id = UUID.randomUUID();
    Role expectedRole = Role.builder()
        .id(id)
        .name(roleName)
        .build();

    when(roleRepository.findRoleById(id)).thenReturn(Optional.of(expectedRole));

    Role result = roleService.findRowRoleByIdOrThrow(id);

    assertEquals(id, result.getId());
    verify(roleRepository).findRoleById(id);

  }

  @Test
  @DisplayName("Роль по id не найдена - выброс исключения")
  void findRoleShouldThrowWhenRoleIdNotFound() {

    when(roleRepository.findRoleById(any())).thenReturn(Optional.empty());

    assertThrows(RoleNotFoundException.class, () -> roleService.findRowRoleByIdOrThrow(UUID.randomUUID()));
  }

}