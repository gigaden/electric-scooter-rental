package ru.gigaden.electric_scooter_rental.repository.impl;

import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import ru.gigaden.electric_scooter_rental.entity.User;
import ru.gigaden.electric_scooter_rental.repository.UserRepository;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

/**
 * Реализация репозитория пользователей
 */
@Repository
public class UserRepositoryImpl extends BaseRepositoryHiber<User, UUID> implements UserRepository {

    protected UserRepositoryImpl() {
        super(User.class);
    }

    @Override
    public User saveUser(User user) {
        return save(user);
    }

    @Override
    public Optional<User> findUserById(UUID id) {
        return Optional.ofNullable(find(id));
    }

    @Override
    public Collection<User> findAllUsers(int page, int size, String sortBy) {
        return findAll(page, size, sortBy);
    }

    @Override
    public User updateUser(User user) {
        return update(user);
    }

    @Override
    public void deleteUserByEntity(User user) {
        delete(user);
    }

    @Override
    public boolean checkUserIsExistById(UUID id) {
        return exists(id);
    }

    /**
     * Проверяем уникальность имени пользователя
     *
     * @param username - имя пользователя
     */
    @Override
    public boolean checkUsernameIsUnique(String username) {
        String jpql = "SELECT u FROM User u WHERE u.username = :username";
        TypedQuery<User> query = entityManager.createQuery(jpql, User.class);
        query.setParameter("username", username);

        return query.getResultList().isEmpty();
    }

    /**
     * Проверяем уникальность email
     *
     * @param email - email
     */
    @Override
    public boolean checkEmailIsUnique(String email) {
        String jpql = "SELECT u FROM User u WHERE u.email = :email";
        TypedQuery<User> query = entityManager.createQuery(jpql, User.class);
        query.setParameter("email", email);

        return query.getResultList().isEmpty();
    }
}
