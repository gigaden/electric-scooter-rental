package ru.gigaden.electric_scooter_rental.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.gigaden.electric_scooter_rental.dto.role.RoleCreateDto;
import ru.gigaden.electric_scooter_rental.dto.role.RoleResponseDto;
import ru.gigaden.electric_scooter_rental.dto.role.RoleUpdateDto;
import ru.gigaden.electric_scooter_rental.service.RoleService;

import java.util.Collection;
import java.util.UUID;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Роли", description = "Контроллер для управления ролями пользователей")
public class RoleController {

    private final RoleService roleService;

    /**
     * Создаём новой роли
     */
    @PostMapping
    @Operation(summary = "Добавление роли", description = "Добавление новой роли в БД")
    public ResponseEntity<RoleResponseDto> addRole(@Valid @RequestBody RoleCreateDto dto) {
        log.info("Создаём новую роль {}", dto);
        RoleResponseDto response = roleService.createRole(dto);

        return ResponseEntity.ok(response);
    }

    /**
     * Получаем все роли. В пагинации смысла особого не вижу для ролей
     */
    @GetMapping
    @Operation(summary = "Получение ролей", description = "Получаем все возможные роли пользователей")
    public ResponseEntity<Collection<RoleResponseDto>> getAllRoles() {
        log.info("Получаем все возможные роли пользователей");
        Collection<RoleResponseDto> response = roleService.findAllRoles();

        return ResponseEntity.ok(response);
    }

    /**
     * Обновляем роль
     */
    @PutMapping("/{roleId}")
    @Operation(summary = "Обновление роли", description = "Обновление роли пользователя по её id")
    public ResponseEntity<RoleResponseDto> updateRoleById(@PathVariable(name = "roleId") UUID roleId,
                                                          @Valid @RequestBody RoleUpdateDto dto) {
        log.info("Обновляем роль пользователя с id {}", roleId);
        RoleResponseDto response = roleService.updateRole(roleId, dto);

        return ResponseEntity.ok(response);
    }

    /**
     * Удаляем роль по id
     */
    @DeleteMapping("/{roleId}")
    @Operation(summary = "Удаление роли", description = "Удаление роли из БД")
    public ResponseEntity<String> deleteRoleById(@PathVariable(name = "roleId") UUID roleId) {
        log.info("Удаляем роль с id = {}", roleId);
        roleService.deleteRoleById(roleId);

        return ResponseEntity.ok("Роль удалена");
    }

}
