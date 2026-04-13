package ru.gigaden.electric_scooter_rental.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.gigaden.electric_scooter_rental.entity.Role;
import ru.gigaden.electric_scooter_rental.exception.RoleNotFoundException;
import ru.gigaden.electric_scooter_rental.repository.RoleRepository;
import ru.gigaden.electric_scooter_rental.service.RoleService;

/**
 * Реализация сервиса для ролей
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

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
                .orElseThrow(() -> {
                    log.error("Роль {} не найдена", name);
                    return new RoleNotFoundException("Роль не найдена");
                });
        log.info("Найдена роль {}", name);

        return role;
    }
}
