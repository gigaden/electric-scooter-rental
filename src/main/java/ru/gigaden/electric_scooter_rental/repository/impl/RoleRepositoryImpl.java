package ru.gigaden.electric_scooter_rental.repository.impl;

import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import ru.gigaden.electric_scooter_rental.entity.Role;
import ru.gigaden.electric_scooter_rental.repository.RoleRepository;

import java.util.Collection;
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


    @Override
    public Role saveRole(Role role) {
        return save(role);
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

    /**
     * Ищет роль по её id
     *
     * @param id - id роли
     * @return - роль, или пустой Optional
     */
    @Override
    public Optional<Role> findRoleById(UUID id) {
        return Optional.ofNullable(find(id));
    }

    /**
     * Обновляем роль
     */
    @Override
    public Role updateRole(Role role) {
        return update(role);
    }

    /**
     * Получаем все возможные роли
     */
    @Override
    public Collection<Role> findAll() {
        String jpql = "SELECT r FROM Role r";
        TypedQuery<Role> query = entityManager.createQuery(jpql, Role.class);

        return query.getResultList();
    }

    /**
     * Метод удаляет роль
     *
     * @param role - роль, которую нужно удалить
     */
    @Override
    public void deleteRole(Role role) {
        delete(role);
    }
}
