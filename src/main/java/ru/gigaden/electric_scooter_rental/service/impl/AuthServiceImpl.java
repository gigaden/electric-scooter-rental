package ru.gigaden.electric_scooter_rental.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import ru.gigaden.electric_scooter_rental.dto.auth.AuthResponse;
import ru.gigaden.electric_scooter_rental.security.CustomUserDetails;
import ru.gigaden.electric_scooter_rental.security.JwtTokenProvider;
import ru.gigaden.electric_scooter_rental.service.AuthService;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    @Override
    public AuthResponse authenticate(String username, String password) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String token = tokenProvider.generateToken(authentication);

        log.info("Пользователь {} успешно аутентифицирован", username);
        return new AuthResponse(token, userDetails.getId());
    }
}