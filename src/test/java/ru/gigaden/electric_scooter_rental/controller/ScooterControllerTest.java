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
import ru.gigaden.electric_scooter_rental.dto.scooter.ScooterCreateDto;
import ru.gigaden.electric_scooter_rental.dto.scooter.ScooterResponseDto;
import ru.gigaden.electric_scooter_rental.dto.scooter.ScooterUpdateDto;
import ru.gigaden.electric_scooter_rental.entity.ScooterSortField;
import ru.gigaden.electric_scooter_rental.entity.ScooterStatus;
import ru.gigaden.electric_scooter_rental.exception.RentalPointNotFoundException;
import ru.gigaden.electric_scooter_rental.exception.ScooterNotFoundException;
import ru.gigaden.electric_scooter_rental.service.ScooterService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ScooterController.class)
@Import(SecurityConfig.class)
@DisplayName("Тесты контроллера самокатов")
class ScooterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ScooterService scooterService;

    private static final UUID POINT_ID = UUID.randomUUID();
    private static final String VALID_MODEL = "Nimbus2000";
    private static final String VALID_DESCRIPTION_CREATE = "Качественный электросамокат для городской аренды с мощным аккумулятором";
    private static final String VALID_DESCRIPTION_UPDATE = "Качественный электросамокат для городской аренды с мощным аккумулятором и улучшенной подвеской";

    @Test
    @DisplayName("POST /scooters - успешное создание самоката")
    void addScooterShouldReturnOk() throws Exception {
        ScooterCreateDto requestDto = ScooterCreateDto.builder()
                .rentalPointId(POINT_ID)
                .model(VALID_MODEL)
                .description(VALID_DESCRIPTION_CREATE)
                .batteryPower(100)
                .mileage(0)
                .build();

        UUID scooterId = UUID.randomUUID();
        ScooterResponseDto responseDto = new ScooterResponseDto(
                scooterId, POINT_ID, 56.3269, 44.0059, VALID_MODEL,
                VALID_DESCRIPTION_CREATE, ScooterStatus.AVAILABLE, 100, 0, LocalDateTime.now()
        );

        when(scooterService.addScooter(any(ScooterCreateDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/scooters")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(scooterId.toString()))
                .andExpect(jsonPath("model").value(VALID_MODEL))
                .andExpect(jsonPath("batteryPower").value(100));

        verify(scooterService, times(1)).addScooter(any(ScooterCreateDto.class));
    }

    @Test
    @DisplayName("POST /scooters - ошибка 400 при невалидном теле запроса")
    void addScooterInvalidRequestShouldReturnBadRequest() throws Exception {
        ScooterCreateDto invalidDto = ScooterCreateDto.builder()
                .rentalPointId(null)
                .model("AB")
                .description("Short")
                .batteryPower(100)
                .mileage(0)
                .build();

        mockMvc.perform(post("/scooters")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());

        verify(scooterService, never()).addScooter(any());
    }

    @Test
    @DisplayName("POST /scooters - ошибка 404 при несуществующей точке аренды")
    void addScooterRentalPointNotFoundShouldReturnNotFound() throws Exception {
        ScooterCreateDto requestDto = ScooterCreateDto.builder()
                .rentalPointId(POINT_ID)
                .model(VALID_MODEL)
                .description(VALID_DESCRIPTION_CREATE)
                .batteryPower(100)
                .mileage(0)
                .build();

        when(scooterService.addScooter(any(ScooterCreateDto.class)))
                .thenThrow(new RentalPointNotFoundException("Точка аренды не найдена"));

        mockMvc.perform(post("/scooters")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isNotFound());

        verify(scooterService, times(1)).addScooter(any());
    }

    @Test
    @DisplayName("GET /scooters/{id} - успешное получение самоката")
    void getScooterShouldReturnOk() throws Exception {
        UUID scooterId = UUID.randomUUID();
        ScooterResponseDto responseDto = new ScooterResponseDto(
                scooterId, POINT_ID, 56.3269, 44.0059, VALID_MODEL,
                VALID_DESCRIPTION_CREATE, ScooterStatus.AVAILABLE, 100, 0, LocalDateTime.now()
        );

        when(scooterService.findScooterById(scooterId)).thenReturn(responseDto);

        mockMvc.perform(get("/scooters/{scooterId}", scooterId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(scooterId.toString()))
                .andExpect(jsonPath("model").value(VALID_MODEL));

        verify(scooterService, times(1)).findScooterById(scooterId);
    }

    @Test
    @DisplayName("GET /scooters/{id} - самокат не найден (404)")
    void getScooterNotFoundShouldReturnNotFound() throws Exception {
        UUID scooterId = UUID.randomUUID();
        when(scooterService.findScooterById(scooterId))
                .thenThrow(new ScooterNotFoundException("Самокат не найден"));

        mockMvc.perform(get("/scooters/{scooterId}", scooterId))
                .andExpect(status().isNotFound());

        verify(scooterService, times(1)).findScooterById(scooterId);
    }

    @Test
    @DisplayName("GET /scooters - получение списка самокатов")
    void findAllScootersShouldReturnOk() throws Exception {
        ScooterResponseDto dto = new ScooterResponseDto(
                UUID.randomUUID(), POINT_ID, 56.3269, 44.0059, VALID_MODEL,
                VALID_DESCRIPTION_CREATE, ScooterStatus.AVAILABLE, 100, 0, LocalDateTime.now()
        );

        when(scooterService.findAll(anyInt(), anyInt(), any()))
                .thenReturn(List.of(dto));

        mockMvc.perform(get("/scooters"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].model").value(VALID_MODEL));

        verify(scooterService).findAll(0, 10, ScooterSortField.BATTERY);
    }

    @Test
    @DisplayName("PUT /scooters/{id} - обновление самоката")
    void updateScooterShouldReturnOk() throws Exception {
        UUID scooterId = UUID.randomUUID();

        ScooterUpdateDto request = new ScooterUpdateDto(
                POINT_ID, 56.33, 44.01, "NewModel", VALID_DESCRIPTION_UPDATE,
                ScooterStatus.MAINTENANCE, 80, 150
        );

        ScooterResponseDto response = new ScooterResponseDto(
                scooterId, POINT_ID, 56.33, 44.01, "NewModel",
                VALID_DESCRIPTION_UPDATE, ScooterStatus.MAINTENANCE, 80, 150, LocalDateTime.now()
        );

        when(scooterService.updateScooterById(eq(scooterId), any())).thenReturn(response);

        mockMvc.perform(put("/scooters/{scooterId}", scooterId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(scooterId.toString()))
                .andExpect(jsonPath("model").value("NewModel"));

        verify(scooterService).updateScooterById(eq(scooterId), any());
    }

    @Test
    @DisplayName("PUT /scooters/{id} - обновление с невалидными данными (400)")
    void updateScooterInvalidDataShouldReturnBadRequest() throws Exception {
        UUID scooterId = UUID.randomUUID();
        ScooterUpdateDto invalidRequest = new ScooterUpdateDto(
                POINT_ID, 56.33, 44.01, "A", "Short",
                ScooterStatus.AVAILABLE, 100, 0
        );

        mockMvc.perform(put("/scooters/{scooterId}", scooterId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(scooterService, never()).updateScooterById(any(), any());
    }

    @Test
    @DisplayName("PUT /scooters/{id} - обновление несуществующего самоката (404)")
    void updateScooterNotFoundShouldReturnNotFound() throws Exception {
        UUID scooterId = UUID.randomUUID();
        ScooterUpdateDto request = new ScooterUpdateDto(
                POINT_ID, 56.33, 44.01, VALID_MODEL, VALID_DESCRIPTION_UPDATE,
                ScooterStatus.AVAILABLE, 100, 0
        );

        when(scooterService.updateScooterById(eq(scooterId), any()))
                .thenThrow(new ScooterNotFoundException("Самокат не найден"));

        mockMvc.perform(put("/scooters/{scooterId}", scooterId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

        verify(scooterService).updateScooterById(eq(scooterId), any());
    }

    @Test
    @DisplayName("DELETE /scooters/{id} - удаление самоката")
    void deleteScooterShouldReturnOk() throws Exception {
        UUID scooterId = UUID.randomUUID();

        mockMvc.perform(delete("/scooters/{scooterId}", scooterId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Самокат удалён"));

        verify(scooterService).deleteScooterById(scooterId);
    }

    @Test
    @DisplayName("DELETE /scooters/{id} - удаление несуществующего самоката (404)")
    void deleteScooterNotFoundShouldReturnNotFound() throws Exception {
        UUID scooterId = UUID.randomUUID();

        doThrow(new ScooterNotFoundException("Самокат не найден"))
                .when(scooterService)
                .deleteScooterById(scooterId);

        mockMvc.perform(delete("/scooters/{scooterId}", scooterId))
                .andExpect(status().isNotFound());

        verify(scooterService).deleteScooterById(scooterId);
    }

    @Test
    @DisplayName("GET /scooters - невалидный параметр сортировки")
    void findAllWithInvalidSortShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/scooters").param("sort", "INVALID"))
                .andExpect(status().isBadRequest());
    }
}