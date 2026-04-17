package ru.gigaden.electric_scooter_rental.service.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
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

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
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

    @Test
    @DisplayName("Получение всех пользователей с пагинацией")
    void findAllShouldBePositive() {
        Role role = Role.builder().name("USER").build();

        User user = User.builder()
                .id(UUID.randomUUID())
                .username("username")
                .email("mail@mail.ru")
                .build();

        UserResponseDto dto = new UserResponseDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                null, null, Set.of(role)
        );

        when(userRepository.findAllUsers(anyInt(), anyInt(), any()))
                .thenReturn(List.of(user));

        when(userMapper.mapUserToResponseDto(user))
                .thenReturn(dto);

        Collection<UserResponseDto> result =
                userService.findAll(0, 10, UserSortField.USERNAME);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Обновление пользователя")
    void updateUserByIdShouldBePositive() {
        UUID id = UUID.randomUUID();

        UserUpdateDto dto = new UserUpdateDto(
                "newUsername",
                "newPassword",
                "new@mail.ru"
        );

        User existingUser = User.builder()
                .id(id)
                .username("old")
                .password("old")
                .email("old@mail.ru")
                .build();

        User updatedUser = User.builder()
                .id(id)
                .username("newUsername")
                .password("encodedPassword")
                .email("new@mail.ru")
                .build();

        UserResponseDto responseDto = new UserResponseDto(
                id,
                "newUsername",
                "new@mail.ru",
                null, null, Set.of()
        );

        when(userRepository.findUserById(id))
                .thenReturn(Optional.of(existingUser));

        when(userRepository.checkUsernameIsUnique(dto.username()))
                .thenReturn(true);

        when(userRepository.checkEmailIsUnique(dto.email()))
                .thenReturn(true);

        when(userRepository.updateUser(any()))
                .thenReturn(updatedUser);

        when(userMapper.mapUserToResponseDto(any()))
                .thenReturn(responseDto);

        UserResponseDto result = userService.updateUserById(id, dto);

        assertNotNull(result);
        assertEquals("newUsername", result.username());

        verify(userRepository).updateUser(any());
    }

    @Test
    @DisplayName("Обновление несуществующего пользователя")
    void updateUserByIdShouldThrowWhenUserNotFound() {
        UUID id = UUID.randomUUID();

        UserUpdateDto dto = new UserUpdateDto(
                "username",
                "password",
                "mail@mail.ru"
        );

        when(userRepository.findUserById(id))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> userService.updateUserById(id, dto));
    }

    @Test
    @DisplayName("Удаление пользователя")
    void deleteUserByIdShouldBePositive() {
        UUID id = UUID.randomUUID();

        User user = User.builder()
                .id(id)
                .build();

        when(userRepository.findUserById(id))
                .thenReturn(Optional.of(user));

        userService.deleteUserById(id);

        verify(userRepository).deleteUserByEntity(user);
    }

    @Test
    @DisplayName("Удаление несуществующего пользователя")
    void deleteUserByIdShouldThrowWhenUserNotFound() {
        UUID id = UUID.randomUUID();

        when(userRepository.findUserById(id))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> userService.deleteUserById(id));
    }
}