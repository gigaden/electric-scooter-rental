package ru.gigaden.electric_scooter_rental.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.gigaden.electric_scooter_rental.config.SecurityConfig;
import ru.gigaden.electric_scooter_rental.dto.role.RoleCreateDto;
import ru.gigaden.electric_scooter_rental.dto.role.RoleResponseDto;
import ru.gigaden.electric_scooter_rental.dto.role.RoleUpdateDto;
import ru.gigaden.electric_scooter_rental.exception.RoleNotFoundException;
import ru.gigaden.electric_scooter_rental.service.RoleService;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RoleController.class)
@Import(SecurityConfig.class)
@DisplayName("Тесты контроллера ролей")
class RoleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private RoleService roleService;

    @Test
    @DisplayName("POST /roles - успешное создание роли")
    void addRoleShouldReturnOk() throws Exception {
        RoleCreateDto requestDto = RoleCreateDto.builder().name("ADMIN").build();
        UUID roleId = UUID.randomUUID();
        RoleResponseDto responseDto = new RoleResponseDto(roleId, "ADMIN");

        when(roleService.createRole(any(RoleCreateDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(roleId.toString()))
                .andExpect(jsonPath("name").value("ADMIN"));

        verify(roleService, times(1)).createRole(any(RoleCreateDto.class));
    }

    @Test
    @DisplayName("POST /roles - ошибка 400 при невалидном теле запроса")
    void addRoleInvalidRequestShouldReturnBadRequest() throws Exception {
        RoleCreateDto invalidDto = RoleCreateDto.builder().name("").build();

        mockMvc.perform(post("/roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());

        verify(roleService, never()).createRole(any());
    }

    @Test
    @DisplayName("PUT /roles/{roleId} - успешное обновление роли")
    void updateRoleShouldReturnOk() throws Exception {
        UUID roleId = UUID.randomUUID();
        RoleUpdateDto requestDto = RoleUpdateDto.builder().name("SUPER_ADMIN").build();
        RoleResponseDto responseDto = new RoleResponseDto(roleId, "SUPER_ADMIN");

        when(roleService.updateRole(any(UUID.class), any(RoleUpdateDto.class))).thenReturn(responseDto);

        mockMvc.perform(put("/roles/{roleId}", roleId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(roleId.toString()))
                .andExpect(jsonPath("name").value("SUPER_ADMIN"));

        verify(roleService).updateRole(eq(roleId), any());
    }

    @Test
    @DisplayName("PUT /roles/{roleId} - ошибка 400 при невалидном теле запроса")
    void updateRoleInvalidRequestShouldReturnBadRequest() throws Exception {
        UUID roleId = UUID.randomUUID();
        RoleUpdateDto invalidDto = RoleUpdateDto.builder().name("").build();

        mockMvc.perform(put("/roles/{roleId}", roleId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());

        verify(roleService, never()).updateRole(any(), any());
    }

    @Test
    @DisplayName("PUT /roles/{roleId} - роль не найдена (404)")
    void updateRoleNotFoundShouldReturnNotFound() throws Exception {
        UUID roleId = UUID.randomUUID();
        RoleUpdateDto requestDto = RoleUpdateDto.builder().name("NEW_NAME").build();

        when(roleService.updateRole(any(UUID.class), any()))
                .thenThrow(new RoleNotFoundException("Роль не найдена"));

        mockMvc.perform(put("/roles/{roleId}", roleId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isNotFound());

        verify(roleService, times(1)).updateRole(eq(roleId), any());
    }

    @Test
    @DisplayName("DELETE /roles/{roleId} - успешное удаление роли")
    void deleteRoleShouldReturnOk() throws Exception {
        UUID roleId = UUID.randomUUID();

        mockMvc.perform(delete("/roles/{roleId}", roleId))
                .andExpect(status().isOk());

        verify(roleService).deleteRoleById(roleId);
    }

    @Test
    @DisplayName("DELETE /roles/{roleId} - роль не найдена (404)")
    void deleteRoleNotFoundShouldReturnNotFound() throws Exception {
        UUID roleId = UUID.randomUUID();

        doThrow(new RoleNotFoundException("Роль не найдена"))
                .when(roleService).deleteRoleById(roleId);

        mockMvc.perform(delete("/roles/{roleId}", roleId))
                .andExpect(status().isNotFound());

        verify(roleService).deleteRoleById(roleId);
    }
}