package com.example.demo.security.jwt;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class JWTUtil {

    private final Key secretKey;

    public JWTUtil(@Value("${spring.jwt.secret}") String secret) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parser()
                .setAllowedClockSkewSeconds(60) // 클라 - 서버 60초 시간차이 허용
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        String userId = claims.get("id", String.class);
        String email = claims.get("email", String.class);
        List<String> roles = claims.get("roles", List.class);

        // 권한 변환
        List<GrantedAuthority> authorities = roles.stream()
                .map(SimpleGrantedAuthority::new)
                .map(grantedAuthority -> (GrantedAuthority) grantedAuthority) // 명시적 캐스팅? 뭔지모름
                .toList();

        // 유저 아이디와 권한을 기반으로 Authentication 객체 생성
        return new UsernamePasswordAuthenticationToken(userId, null, authorities);
    }

    public String createAccessToken(Long id, String email) {
        // 토큰 생성
        return Jwts.builder()
                .claim("id", id.toString())
                .claim("email", email)
                .claim("roles", List.of("USER"))
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 60 * 60 * 1000)) // 만료시간 60분
                .signWith(secretKey)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            // 토큰 파싱 및 서명 검증
            Jwts.parser()
                    .setSigningKey(secretKey) // 비밀키 설정
                    .build()
                    .parseClaimsJws(token);
            return true; // 검증 성공 시 true 반환
        } catch (ExpiredJwtException e) {
            log.error("JWT expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("Malformed JWT: {}", e.getMessage());
        } catch (SignatureException e) {
            log.error("Invalid signature: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("Illegal argument token: {}", e.getMessage());
        }
        return false; // 예외 발생 시 false 반환
    }

}