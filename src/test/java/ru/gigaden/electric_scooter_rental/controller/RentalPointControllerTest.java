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
import ru.gigaden.electric_scooter_rental.dto.point.RentalPointCreateDto;
import ru.gigaden.electric_scooter_rental.dto.point.RentalPointResponseDto;
import ru.gigaden.electric_scooter_rental.dto.point.RentalPointUpdateDto;
import ru.gigaden.electric_scooter_rental.entity.RentalPointSortField;
import ru.gigaden.electric_scooter_rental.exception.RentalPointNotFoundException;
import ru.gigaden.electric_scooter_rental.service.RentalPointService;

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

@WebMvcTest(RentalPointController.class)
@Import(SecurityConfig.class)
@DisplayName("Тесты контроллера точек аренды")
class RentalPointControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private RentalPointService rentalPointService;

  @Test
  @DisplayName("POST /points - успешное создание точки аренды")
  void addRentalPointShouldReturnOk() throws Exception {
    RentalPointCreateDto requestDto = RentalPointCreateDto.builder()
        .address("Н.Новгород, Бурнаковская 103")
        .latitude(56.3269)
        .longitude(44.0059)
        .description("Описание точки аренды")
        .build();

    UUID pointId = UUID.randomUUID();
    RentalPointResponseDto responseDto = new RentalPointResponseDto(
        pointId,
        "Н.Новгород, Бурнаковская 103",
        56.3269,
        44.0059,
        "Описание точки аренды",
        LocalDateTime.now(),
        LocalDateTime.now(),
        List.of()
    );

    when(rentalPointService.addRentalPoint(any(RentalPointCreateDto.class))).thenReturn(responseDto);

    mockMvc.perform(post("/points")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestDto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("id").value(pointId.toString()))
        .andExpect(jsonPath("address").value("Н.Новгород, Бурнаковская 103"))
        .andExpect(jsonPath("latitude").value(56.3269))
        .andExpect(jsonPath("longitude").value(44.0059));

    verify(rentalPointService, times(1)).addRentalPoint(any(RentalPointCreateDto.class));
  }

  @Test
  @DisplayName("POST /points - ошибка 400 при невалидном теле запроса")
  void addRentalPointInvalidRequestShouldReturnBadRequest() throws Exception {
    RentalPointCreateDto invalidDto = RentalPointCreateDto.builder()
        .address("")
        .latitude(56.3269)
        .longitude(44.0059)
        .description("Описание")
        .build();

    mockMvc.perform(post("/points")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidDto)))
        .andExpect(status().isBadRequest());

    verify(rentalPointService, never()).addRentalPoint(any());
  }

  @Test
  @DisplayName("GET /points/{pointId} - успешное получение точки аренды")
  void getRentalPointShouldReturnOk() throws Exception {
    UUID pointId = UUID.randomUUID();
    RentalPointResponseDto responseDto = new RentalPointResponseDto(
        pointId,
        "Н.Новгород, Бурнаковская 103",
        56.3269,
        44.0059,
        "Описание точки аренды",
        LocalDateTime.now(),
        LocalDateTime.now(),
        List.of()
    );

    when(rentalPointService.findRentalPointById(pointId)).thenReturn(responseDto);

    mockMvc.perform(get("/points/{pointId}", pointId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("id").value(pointId.toString()))
        .andExpect(jsonPath("address").value("Н.Новгород, Бурнаковская 103"));

    verify(rentalPointService, times(1)).findRentalPointById(pointId);
  }

  @Test
  @DisplayName("GET /points/{pointId} - точка аренды не найдена (404)")
  void getRentalPointNotFoundShouldReturnNotFound() throws Exception {
    UUID pointId = UUID.randomUUID();
    when(rentalPointService.findRentalPointById(pointId))
        .thenThrow(new RentalPointNotFoundException("Точка аренды не найдена"));

    mockMvc.perform(get("/points/{pointId}", pointId))
        .andExpect(status().isNotFound());

    verify(rentalPointService, times(1)).findRentalPointById(pointId);
  }

  @Test
  @DisplayName("GET /points - получение списка точек аренды")
  void findAllRentalPointsShouldReturnOk() throws Exception {
    RentalPointResponseDto dto = new RentalPointResponseDto(
        UUID.randomUUID(),
        "Н.Новгород, Бурнаковская 103",
        56.3269,
        44.0059,
        "Описание точки аренды",
        LocalDateTime.now(),
        LocalDateTime.now(),
        List.of()
    );

    when(rentalPointService.findAllRentalPoints(anyInt(), anyInt(), any()))
        .thenReturn(List.of(dto));

    mockMvc.perform(get("/points"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].address").value("Н.Новгород, Бурнаковская 103"));

    verify(rentalPointService).findAllRentalPoints(0, 10, RentalPointSortField.TOTAL_SCOOTERS);
  }

  @Test
  @DisplayName("PUT /points/{id} - обновление точки аренды")
  void updateRentalPointShouldReturnOk() throws Exception {
    UUID id = UUID.randomUUID();

    RentalPointUpdateDto request = new RentalPointUpdateDto(
        "Н.Новгород, новая улица 1",
        56.3300,
        44.0100,
        "Обновлённое описание"
    );

    RentalPointResponseDto response = new RentalPointResponseDto(
        id,
        "Н.Новгород, новая улица 1",
        56.3300,
        44.0100,
        "Обновлённое описание",
        LocalDateTime.now(),
        LocalDateTime.now(),
        List.of()
    );

    when(rentalPointService.updateRentalPoint(eq(id), any())).thenReturn(response);

    mockMvc.perform(put("/points/{id}", id)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("id").value(id.toString()))
        .andExpect(jsonPath("address").value("Н.Новгород, новая улица 1"));

    verify(rentalPointService).updateRentalPoint(eq(id), any());
  }

  @Test
  @DisplayName("PUT /points/{id} - обновление несуществующей точки (404)")
  void updateRentalPointNotFoundShouldReturnNotFound() throws Exception {
    UUID id = UUID.randomUUID();
    RentalPointUpdateDto request = new RentalPointUpdateDto(
        "Адрес Точки аренды",
        56.3269,
        44.0059,
        "Описание точки аренды"
    );

    when(rentalPointService.updateRentalPoint(eq(id), any()))
        .thenThrow(new RentalPointNotFoundException("Точка аренды не найдена"));

    mockMvc.perform(put("/points/{id}", id)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isNotFound());

    verify(rentalPointService).updateRentalPoint(eq(id), any());
  }

  @Test
  @DisplayName("DELETE /points/{id} - удаление точки аренды")
  void deleteRentalPointShouldReturnOk() throws Exception {
    UUID id = UUID.randomUUID();

    mockMvc.perform(delete("/points/{id}", id))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").value("Точка аренды удалена"));

    verify(rentalPointService).deleteRentalPointById(id);
  }

  @Test
  @DisplayName("DELETE /points/{id} - удаление несуществующей точки (404)")
  void deleteRentalPointNotFoundShouldReturnNotFound() throws Exception {
    UUID id = UUID.randomUUID();
    doThrow(new RentalPointNotFoundException("Точка аренды не найдена"))
        .when(rentalPointService)
        .deleteRentalPointById(id);

    mockMvc.perform(delete("/points/{id}", id))
        .andExpect(status().isNotFound());

    verify(rentalPointService).deleteRentalPointById(id);
  }
}