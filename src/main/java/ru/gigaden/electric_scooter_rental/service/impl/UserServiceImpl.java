package ru.gigaden.electric_scooter_rental.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.gigaden.electric_scooter_rental.dto.UserCreateDto;
import ru.gigaden.electric_scooter_rental.dto.UserResponseDto;
import ru.gigaden.electric_scooter_rental.entity.Role;
import ru.gigaden.electric_scooter_rental.entity.User;
import ru.gigaden.electric_scooter_rental.exception.UserNotFoundException;
import ru.gigaden.electric_scooter_rental.exception.UserNotUniqueException;
import ru.gigaden.electric_scooter_rental.mapper.UserMapper;
import ru.gigaden.electric_scooter_rental.repository.UserRepository;
import ru.gigaden.electric_scooter_rental.service.RoleService;
import ru.gigaden.electric_scooter_rental.service.UserService;

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
        checkUsersEmailAndUsername(dto);

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
     * @throws ru.gigaden.electric_scooter_rental.exception.UserNotFoundException если пользователь не найден
     */
    @Override
    public UserResponseDto getUserById(UUID id) {
        User user = getRowUserOrThrow(id);
        UserResponseDto response = userMapper.mapUserToResponseDto(user);
        log.info("Получили пользователя с id = {}", id);

        return response;
    }

    /**
     * Метод получает по незамапенный объект пользователя
     */
    private User getRowUserOrThrow(UUID id) {
        return userRepository.findUserById(id)
                .orElseThrow(() -> {
                    log.error("Пользователь id = {} не найден", id);
                    return new UserNotFoundException("Пользователь не найден");
                });
    }

    /**
     * Проверяем уникальность имени пользователя и его email
     */
    private void checkUsersEmailAndUsername(UserCreateDto dto) {
        if (!userRepository.checkEmailIsUnique(dto.email())) {
            log.error("Email пользователя не уникален {}", dto.email());
            throw new UserNotUniqueException("Email не уникален");
        }
        if (!userRepository.checkUsernameIsUnique(dto.username())) {
            log.warn("Имя пользователя не уникально {}", dto.username());
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
