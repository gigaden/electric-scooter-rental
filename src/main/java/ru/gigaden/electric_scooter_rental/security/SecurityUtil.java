package ru.gigaden.electric_scooter_rental.security;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Содержит методы для работы с безопасностью
 */
@Component
public class SecurityUtil {

  public CustomUserDetails getCurrentUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !authentication.isAuthenticated()) {
      return null;
    }
    return (CustomUserDetails) authentication.getPrincipal();
  }

  public UUID getCurrentUserId() {
    CustomUserDetails user = getCurrentUser();
    return user != null ? user.getId() : null;
  }

  public boolean hasRole(String role) {
    CustomUserDetails user = getCurrentUser();
    return user != null && user.getRoles().contains("ROLE_" + role);
  }

  public boolean isAdmin() {
    return hasRole("ADMIN");
  }

  public boolean isOwner(UUID resourceOwnerId) {
    UUID currentUserId = getCurrentUserId();
    return currentUserId != null && currentUserId.equals(resourceOwnerId);
  }

  public void checkOwnerOrAdmin(UUID resourceOwnerId) {
    if (!isAdmin() && !isOwner(resourceOwnerId)) {
      throw new AccessDeniedException("Доступ запрещён");
    }
  }
}