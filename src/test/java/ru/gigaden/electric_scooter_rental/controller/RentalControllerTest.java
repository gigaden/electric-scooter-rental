package ru.gigaden.electric_scooter_rental.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.gigaden.electric_scooter_rental.TestSecurityConfig;
import ru.gigaden.electric_scooter_rental.dto.rental.RentalCreateDto;
import ru.gigaden.electric_scooter_rental.dto.rental.RentalResponseDto;
import ru.gigaden.electric_scooter_rental.entity.RentalStatus;
import ru.gigaden.electric_scooter_rental.exception.RentalCompleteException;
import ru.gigaden.electric_scooter_rental.exception.RentalNotFoundException;
import ru.gigaden.electric_scooter_rental.security.CustomUserDetailsService;
import ru.gigaden.electric_scooter_rental.security.JwtTokenProvider;
import ru.gigaden.electric_scooter_rental.security.SecurityUtil;
import ru.gigaden.electric_scooter_rental.service.RentalService;
import ru.gigaden.electric_scooter_rental.service.UserSubscriptionService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RentalController.class)
@Import(TestSecurityConfig.class)
@DisplayName("Тесты контроллера аренд")
class RentalControllerTest {

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private ObjectMapper objectMapper;
  @MockitoBean
  private RentalService rentalService;
  @MockitoBean
  private SecurityUtil securityUtil;
  @MockitoBean
  private JwtTokenProvider jwtTokenProvider;
  @MockitoBean
  private CustomUserDetailsService customUserDetailsService;
  @MockitoBean
  private UserSubscriptionService userSubscriptionService;

  private static final UUID USER_ID = UUID.randomUUID();
  private static final UUID SCOOTER_ID = UUID.randomUUID();
  private static final UUID RENTAL_ID = UUID.randomUUID();

  @Test
  @DisplayName("POST /rentals - создание аренды")
  @WithMockUser(username = "user1", roles = {"USER"})
  void createRentalShouldReturnCreated() throws Exception {
    RentalCreateDto request = new RentalCreateDto(USER_ID, SCOOTER_ID, 100, null, null);
    RentalResponseDto response = new RentalResponseDto(RENTAL_ID, USER_ID, SCOOTER_ID, RentalStatus.IN_PROGRESS, LocalDateTime.now(), null, 100, null, null, null, null);

    when(rentalService.createRental(any(RentalCreateDto.class))).thenReturn(response);

    mockMvc.perform(post("/rentals").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("id").value(RENTAL_ID.toString()));
    verify(rentalService).createRental(any(RentalCreateDto.class));
  }

  @Test
  @DisplayName("POST /rentals - без аутентификации (401)")
  void createRentalWithoutAuthShouldReturnUnauthorized() throws Exception {
    RentalCreateDto request = new RentalCreateDto(USER_ID, SCOOTER_ID, 100, null, null);

    mockMvc.perform(post("/rentals").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isForbidden());
    verify(rentalService, never()).createRental(any());
  }

  @Test
  @DisplayName("GET /rentals/{id} - получение аренды")
  @WithMockUser(username = "user1", roles = {"USER"})
  void getRentalByIdShouldReturnOk() throws Exception {
    RentalResponseDto response = new RentalResponseDto(RENTAL_ID, USER_ID, SCOOTER_ID, RentalStatus.IN_PROGRESS, LocalDateTime.now(), null, 100, null, null, null, null);
    when(rentalService.findRentalById(RENTAL_ID)).thenReturn(response);

    mockMvc.perform(get("/rentals/{rentalId}", RENTAL_ID))
        .andExpect(status().isOk())
        .andExpect(jsonPath("id").value(RENTAL_ID.toString()));
    verify(rentalService).findRentalById(RENTAL_ID);
  }

  @Test
  @DisplayName("GET /rentals/{id} - аренда не найдена (404)")
  @WithMockUser(username = "user1", roles = {"USER"})
  void getRentalByIdNotFoundShouldReturnNotFound() throws Exception {
    when(rentalService.findRentalById(RENTAL_ID)).thenThrow(new RentalNotFoundException("Аренда не найдена"));
    mockMvc.perform(get("/rentals/{rentalId}", RENTAL_ID)).andExpect(status().isNotFound());
    verify(rentalService).findRentalById(RENTAL_ID);
  }

  @Test
  @DisplayName("GET /rentals - список аренд (только админ)")
  @WithMockUser(username = "admin", roles = {"ADMIN"})
  void findAllRentalsAsAdminShouldReturnOk() throws Exception {
    RentalResponseDto dto = new RentalResponseDto(RENTAL_ID, USER_ID, SCOOTER_ID, RentalStatus.IN_PROGRESS, LocalDateTime.now(), null, 100, null, null, null, null);
    when(rentalService.findAllRentals(anyInt(), anyInt())).thenReturn(List.of(dto));

    mockMvc.perform(get("/rentals")).andExpect(status().isOk()).andExpect(jsonPath("$[0].id").value(RENTAL_ID.toString()));
    verify(rentalService).findAllRentals(0, 10);
  }

  @Test
  @DisplayName("GET /rentals - не админ (403)")
  @WithMockUser(username = "user1", roles = {"USER"})
  void findAllRentalsAsUserShouldReturnForbidden() throws Exception {
    mockMvc.perform(get("/rentals")).andExpect(status().isForbidden());
    verify(rentalService, never()).findAllRentals(anyInt(), anyInt());
  }

  @Test
  @DisplayName("POST /rentals/{id}/complete - завершение аренды")
  @WithMockUser(username = "user1", roles = {"USER"})
  void completeRentalShouldReturnOk() throws Exception {
    RentalResponseDto response = new RentalResponseDto(RENTAL_ID, USER_ID, SCOOTER_ID, RentalStatus.FINISHED, LocalDateTime.now(), LocalDateTime.now(), 100, 150, null, null, BigDecimal.valueOf(500));
    when(rentalService.completeRental(RENTAL_ID)).thenReturn(response);

    mockMvc.perform(post("/rentals/{rentalId}/complete", RENTAL_ID))
        .andExpect(status().isOk())
        .andExpect(jsonPath("status").value("FINISHED"));
    verify(rentalService).completeRental(RENTAL_ID);
  }

  @Test
  @DisplayName("POST /rentals/{id}/complete - аренда уже завершена (409)")
  @WithMockUser(username = "user1", roles = {"USER"})
  void completeRentalAlreadyFinishedShouldReturnConflict() throws Exception {
    when(rentalService.completeRental(RENTAL_ID)).thenThrow(new RentalCompleteException("Аренда не является активной"));
    mockMvc.perform(post("/rentals/{rentalId}/complete", RENTAL_ID)).andExpect(status().isConflict());
    verify(rentalService).completeRental(RENTAL_ID);
  }

  @Test
  @DisplayName("GET /rentals/scooter/{scooterId} - история самоката (админ)")
  @WithMockUser(username = "admin", roles = {"ADMIN"})
  void getRentalHistoryByScooterAsAdminShouldReturnOk() throws Exception {
    RentalResponseDto dto = new RentalResponseDto(RENTAL_ID, USER_ID, SCOOTER_ID, RentalStatus.FINISHED, LocalDateTime.now(), LocalDateTime.now(), 100, 150, null, null, BigDecimal.valueOf(500));
    when(rentalService.findFinishedRentalsByScooterId(SCOOTER_ID, 0, 10)).thenReturn(List.of(dto));

    mockMvc.perform(get("/rentals/scooter/{scooterId}", SCOOTER_ID)).andExpect(status().isOk());
    verify(rentalService).findFinishedRentalsByScooterId(SCOOTER_ID, 0, 10);
  }

  @Test
  @DisplayName("GET /rentals/scooter/{scooterId} - не админ (403)")
  @WithMockUser(username = "user1", roles = {"USER"})
  void getRentalHistoryByScooterAsUserShouldReturnForbidden() throws Exception {
    mockMvc.perform(get("/rentals/scooter/{scooterId}", SCOOTER_ID)).andExpect(status().isForbidden());
    verify(rentalService, never()).findFinishedRentalsByScooterId(any(), anyInt(), anyInt());
  }
}