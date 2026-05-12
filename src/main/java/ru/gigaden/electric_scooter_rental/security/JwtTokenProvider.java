package ru.gigaden.electric_scooter_rental.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.UUID;

/**
 * Методы для работы с jwt
 */
@Component
public class JwtTokenProvider {

  private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

  @Value("${jwt.expiration:86400000}")
  private long jwtExpirationMs;

  public String generateToken(Authentication authentication) {
    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
    Date now = new Date();
    Date expiry = new Date(now.getTime() + jwtExpirationMs);

    return Jwts.builder()
        .setSubject(userDetails.getId().toString())
        .claim("username", userDetails.getUsername())
        .claim("roles", userDetails.getRoles())
        .setIssuedAt(now)
        .setExpiration(expiry)
        .signWith(key)
        .compact();
  }

  public UUID extractUserId(String token) {
    Claims claims = Jwts.parserBuilder()
        .setSigningKey(key)
        .build()
        .parseClaimsJws(token)
        .getBody();
    return UUID.fromString(claims.getSubject());
  }

  public boolean validateToken(String token) {
    try {
      Jwts.parserBuilder()
          .setSigningKey(key)
          .build()
          .parseClaimsJws(token);
      return true;
    } catch (Exception e) {
      return false;
    }
  }
}