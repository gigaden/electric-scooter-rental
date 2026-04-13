package ru.gigaden.electric_scooter_rental.service.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.gigaden.electric_scooter_rental.dto.UserCreateDto;
import ru.gigaden.electric_scooter_rental.dto.UserResponseDto;
import ru.gigaden.electric_scooter_rental.entity.Role;
import ru.gigaden.electric_scooter_rental.entity.User;
import ru.gigaden.electric_scooter_rental.exception.UserNotFoundException;
import ru.gigaden.electric_scooter_rental.exception.UserNotUniqueException;
import ru.gigaden.electric_scooter_rental.mapper.UserMapper;
import ru.gigaden.electric_scooter_rental.repository.UserRepository;
import ru.gigaden.electric_scooter_rental.service.RoleService;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тесты сервиса пользователей")
class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleService roleService;

    @Test
    @DisplayName("Добавление нового пользователя")
    void addUserShouldBePositive() {
        UserCreateDto dto = UserCreateDto.builder()
                .username("username")
                .password("password")
                .email("mail@mail.ru")
                .build();
        Role role = Role.builder().name("USER").build();
        User userBeforeSave = User.builder()
                .username("username")
                .password("encodedPassword")
                .email("mail@mail.ru")
                .build();
        User savedUser = User.builder()
                .id(UUID.randomUUID())
                .username("username")
                .password("encodedPassword")
                .email("mail@mail.ru")
                .build();
        UserResponseDto responseDto = new UserResponseDto(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getEmail(),
                null, null, Set.of(role));

        when(userRepository.checkEmailIsUnique(dto.email())).thenReturn(true);
        when(userRepository.checkUsernameIsUnique(dto.username())).thenReturn(true);
        when(roleService.findRowRoleByNameOrThrow("USER")).thenReturn(role);
        when(passwordEncoder.encode(dto.password())).thenReturn("encodedPassword");
        when(userMapper.mapCreateDtoToUser(dto)).thenReturn(userBeforeSave);
        when(userRepository.saveUser(userBeforeSave)).thenReturn(savedUser);
        when(userMapper.mapUserToResponseDto(savedUser)).thenReturn(responseDto);

        UserResponseDto result = userService.addUser(dto);

        assertNotNull(result);
        assertEquals(dto.username(), result.username());
        verify(userRepository).saveUser(userBeforeSave);
    }

    @Test
    @DisplayName("Получение пользователя по id")
    void getUserByIdShouldBePositive() {
        Role role = Role.builder().name("USER").build();
        UUID id = UUID.randomUUID();
        User savedUser = User.builder()
                .id(id)
                .username("username")
                .password("encodedPassword")
                .email("mail@mail.ru")
                .build();
        UserResponseDto responseDto = new UserResponseDto(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getEmail(),
                null, null, Set.of(role));

        when(userRepository.findUserById(any())).thenReturn(Optional.of(savedUser));
        when(userMapper.mapUserToResponseDto(savedUser)).thenReturn(responseDto);

        UserResponseDto result = userService.findUserById(id);

        assertNotNull(result);
        assertEquals(savedUser.getUsername(), result.username());
        verify(userRepository).findUserById(id);
    }

    @Test
    @DisplayName("Добавление пользователя с неуникальным email должно бросать исключение")
    void addUserShouldThrowWhenEmailNotUnique() {
        UserCreateDto dto = UserCreateDto.builder()
                .username("username")
                .password("password")
                .email("duplicate@mail.ru")
                .build();

        when(userRepository.checkEmailIsUnique(dto.email())).thenReturn(false);

        assertThrows(UserNotUniqueException.class, () -> userService.addUser(dto));
        verify(userRepository, never()).saveUser(any());
    }

    @Test
    @DisplayName("Добавление пользователя с неуникальным username должно бросать исключение")
    void addUserShouldThrowWhenUsernameNotUnique() {
        UserCreateDto dto = UserCreateDto.builder()
                .username("duplicate")
                .password("password")
                .email("mail@mail.ru")
                .build();

        when(userRepository.checkEmailIsUnique(dto.email())).thenReturn(true);
        when(userRepository.checkUsernameIsUnique(dto.username())).thenReturn(false);

        assertThrows(UserNotUniqueException.class, () -> userService.addUser(dto));
        verify(userRepository, never()).saveUser(any());
    }

    @Test
    @DisplayName("Поиск пользователя по несуществующему ID должен бросать исключение")
    void findUserByIdShouldThrowWhenUserNotFound() {
        UUID id = UUID.randomUUID();
        when(userRepository.findUserById(id)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.findUserById(id));
        verify(userMapper, never()).mapUserToResponseDto(any());
    }
}