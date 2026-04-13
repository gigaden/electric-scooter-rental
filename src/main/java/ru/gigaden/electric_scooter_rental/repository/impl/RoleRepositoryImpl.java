package ru.gigaden.electric_scooter_rental.repository.impl;

import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import ru.gigaden.electric_scooter_rental.entity.Role;
import ru.gigaden.electric_scooter_rental.repository.RoleRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * Реализация репозитория ролей пользователя
 */
@Repository
public class RoleRepositoryImpl extends BaseRepositoryHiber<Role, UUID> implements RoleRepository {

    protected RoleRepositoryImpl() {
        super(Role.class);
    }

    /**
     * Ищет роль по её имени
     *
     * @param name - название роли
     * @return - роль, или пустой Optional
     */
    @Override
    public Optional<Role> findRoleByName(String name) {
        String jpql = "SELECT r FROM Role r WHERE name = :name";
        TypedQuery<Role> query = entityManager.createQuery(jpql, Role.class);
        query.setParameter("name", name);
        Role role = query.getSingleResult();

        return Optional.ofNullable(role);
    }
}
