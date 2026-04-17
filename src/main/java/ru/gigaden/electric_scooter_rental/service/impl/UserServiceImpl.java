package ru.gigaden.electric_scooter_rental.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.gigaden.electric_scooter_rental.dto.user.UserCreateDto;
import ru.gigaden.electric_scooter_rental.dto.user.UserResponseDto;
import ru.gigaden.electric_scooter_rental.dto.user.UserUpdateDto;
import ru.gigaden.electric_scooter_rental.entity.Role;
import ru.gigaden.electric_scooter_rental.entity.User;
import ru.gigaden.electric_scooter_rental.entity.UserSortField;
import ru.gigaden.electric_scooter_rental.exception.UserNotFoundException;
import ru.gigaden.electric_scooter_rental.exception.UserNotUniqueException;
import ru.gigaden.electric_scooter_rental.mapper.UserMapper;
import ru.gigaden.electric_scooter_rental.repository.UserRepository;
import ru.gigaden.electric_scooter_rental.service.RoleService;
import ru.gigaden.electric_scooter_rental.service.UserService;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Основная реализация сервиса пользователей
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleService roleService;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;


    /**
     * Создание нового клиента
     *
     * @param dto - дто для нового пользователя
     * @return - дто с созданным пользователем
     */
    @Transactional
    @Override
    public UserResponseDto addUser(UserCreateDto dto) {
        checkUserUsername(dto.username());
        checkUserEmail(dto.email());

        User newUser = prepareNewUser(dto);
        User savedUser = userRepository.saveUser(newUser);

        UserResponseDto response = userMapper.mapUserToResponseDto(savedUser);
        log.info("Добавлен новый пользователь {}", response);

        return response;
    }

    /**
     * Метод возвращает пользователя по его id
     *
     * @param id - id пользователя для поиска
     * @return - дто с пользователем
     * @throws UserNotFoundException если пользователь не найден
     */
    @Override
    public UserResponseDto findUserById(UUID id) {
        User user = findRowUserOrThrow(id);
        UserResponseDto response = userMapper.mapUserToResponseDto(user);
        log.info("Получили пользователя с id = {}", id);

        return response;
    }

    /**
     * Получаем всех пользователей с пагинацией и сортировкой
     *
     * @param page - номер страницы
     * @param size - размер
     * @param sort - поле, по которому сортируем
     */
    @Override
    public Collection<UserResponseDto> findAll(int page, int size, UserSortField sort) {
        Collection<UserResponseDto> response = userRepository.findAllUsers(page, size, sort.getField()).stream()
                .map(userMapper::mapUserToResponseDto).toList();
        log.info("Получили список пользователей в количестве {}", response.size());

        return response;
    }

    /**
     * Метод обновляет пользователя по его id
     *
     * @param userId - id пользователя
     * @param dto    - данные для обновления
     * @throws UserNotFoundException - если пользователь не найден
     */
    @Transactional
    @Override
    public UserResponseDto updateUserById(UUID userId, UserUpdateDto dto) {
        User existingUser = findRowUserOrThrow(userId);
        updateUserFields(existingUser, dto);
        User updatedUser = userRepository.updateUser(existingUser);
        UserResponseDto response = userMapper.mapUserToResponseDto(updatedUser);
        log.info("Пользователь с id {} обновлён", userId);

        return response;
    }

    /**
     * Метод удаляет пользователя по его id
     *
     * @param id - id пользователя
     * @throws UserNotFoundException - если пользователь не найден
     */
    @Transactional
    @Override
    public void deleteUserById(UUID id) {
        User user = findRowUserOrThrow(id);
        userRepository.deleteUserByEntity(user);
        log.info("Пользователь с id {} удалён", id);
    }

    /**
     * Обновляем сущность пользователя данными из дто
     */
    private void updateUserFields(User existingUser, UserUpdateDto dto) {
        if (dto.username() != null) {
            checkUserUsername(dto.username());
            existingUser.setUsername(dto.username());
        }
        if (dto.email() != null) {
            checkUserEmail(dto.email());
            existingUser.setEmail(dto.email());
        }
        if (dto.password() != null) {
            existingUser.setPassword(dto.password());
        }
    }

    /**
     * Метод получает по незамапенный объект пользователя
     */
    private User findRowUserOrThrow(UUID id) {
        return userRepository.findUserById(id)
                .orElseThrow(() -> {
                    log.error("Пользователь id = {} не найден", id);
                    return new UserNotFoundException("Пользователь не найден");
                });
    }

    /**
     * Проверяем уникальность email
     */
    private void checkUserEmail(String email) {
        if (!userRepository.checkEmailIsUnique(email)) {
            log.error("Email пользователя не уникален {}", email);
            throw new UserNotUniqueException("Email не уникален");
        }
    }

    /**
     * Проверяем уникальность имени пользователя
     */
    private void checkUserUsername(String username) {
        if (!userRepository.checkUsernameIsUnique(username)) {
            log.warn("Имя пользователя не уникально {}", username);
            throw new UserNotUniqueException("Имя пользователя не уникально");
        }
    }

    /**
     * Метод подготавливает нового пользователя для сохранения
     *
     * @param dto - дто пользователя
     * @return - готовый объект для сохранения
     */
    private User prepareNewUser(UserCreateDto dto) {
        User user = userMapper.mapCreateDtoToUser(dto);

        String encodedPassword = passwordEncoder.encode(dto.password());
        user.setPassword(encodedPassword);

        Role defaultRole = roleService.findRowRoleByNameOrThrow("USER");
        user.setRoles(new HashSet<>(Set.of(defaultRole)));

        return user;
    }
}
