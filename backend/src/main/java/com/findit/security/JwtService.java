package com.findit.security;

import com.findit.model.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {
  private final SecretKey key;
  public JwtService(@Value("${findit.jwt.secret}") String secret) {
    if (secret.getBytes(StandardCharsets.UTF_8).length < 32) throw new IllegalArgumentException("JWT secret must be at least 32 bytes");
    this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
  }
  public String create(User user) {
    return Jwts.builder().subject(user.email).claim("role", user.role.name())
      .issuedAt(new Date()).expiration(new Date(System.currentTimeMillis()+86400000))
      .signWith(key).compact();
  }
  public String subject(String token) {
    return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().getSubject();
  }
}
