package ru.gigaden.electric_scooter_rental.mapper;

import org.mapstruct.Mapper;
import ru.gigaden.electric_scooter_rental.dto.user.UserCreateDto;
import ru.gigaden.electric_scooter_rental.dto.user.UserResponseDto;
import ru.gigaden.electric_scooter_rental.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User mapCreateDtoToUser(UserCreateDto dto);

    UserResponseDto mapUserToResponseDto(User user);
}