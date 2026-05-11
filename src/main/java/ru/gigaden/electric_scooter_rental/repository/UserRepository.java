package ru.gigaden.electric_scooter_rental.repository;

import ru.gigaden.electric_scooter_rental.entity.User;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

/**
 * Репозиторий для работы с пользователями
 */
public interface UserRepository {

  User saveUser(User user);

  Optional<User> findUserById(UUID id);

  Optional<User> findUserByUsername(String username);

  Collection<User> findAllUsers(int page, int size, String sortBy);

  User updateUser(User user);

  void deleteUserByEntity(User user);

  boolean checkUserIsExistById(UUID id);

  boolean checkUsernameIsUnique(String username);

  boolean checkEmailIsUnique(String email);
}