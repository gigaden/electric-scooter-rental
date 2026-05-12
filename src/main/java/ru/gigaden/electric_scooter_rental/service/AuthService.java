package ru.gigaden.electric_scooter_rental.service;

import ru.gigaden.electric_scooter_rental.dto.auth.AuthResponse;

public interface AuthService {
  AuthResponse authenticate(String username, String password);
}