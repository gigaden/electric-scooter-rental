package ru.gigaden.electric_scooter_rental.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.gigaden.electric_scooter_rental.repository.UserRepository;

import java.util.UUID;

/**
 * Получаем пользователя из безопасности
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return userRepository.findUserByUsername(username)
        .map(CustomUserDetails::new)
        .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден: " + username));
  }

  public UserDetails loadUserById(UUID userId) throws UsernameNotFoundException {
    return userRepository.findUserById(userId)
        .map(CustomUserDetails::new)
        .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден: " + userId));
  }
}