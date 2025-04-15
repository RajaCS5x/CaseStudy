package com.movie.login_serivce.util;

import java.util.Date;

import org.springframework.stereotype.Component;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtUtil {

  private final String jwtsecret = "your_secret_key_here_make_it_long_and_secure_for_better_security"; // 256-bit key
  private final long jwtExpiration = 86400000; // 1 day in milliseconds

  public String generateToken(String username, String role) {
    Key key = new SecretKeySpec(jwtsecret.getBytes(), SignatureAlgorithm.HS256.getJcaName());
    return Jwts.builder()
        .setSubject(username)
        .claim("role", role)
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
        .signWith(key, SignatureAlgorithm.HS256)
        .compact();

  }
}
