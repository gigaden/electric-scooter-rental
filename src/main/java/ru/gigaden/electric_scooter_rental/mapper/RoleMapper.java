package ru.gigaden.electric_scooter_rental.mapper;

import org.mapstruct.Mapper;
import ru.gigaden.electric_scooter_rental.dto.role.RoleCreateDto;
import ru.gigaden.electric_scooter_rental.dto.role.RoleResponseDto;
import ru.gigaden.electric_scooter_rental.entity.Role;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    Role mapCreateDtoToRole(RoleCreateDto dto);

    RoleResponseDto mapRoleToResponseDto(Role role);
}