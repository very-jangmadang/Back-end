package com.example.demo.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class JWTUtil {

    private final Key secretKey;

    public JWTUtil(@Value("${spring.jwt.secret}") String secret) {
        this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());

    }
    // 엑세스 토큰 생성
    public String createAccessToken(String category, Long id, String email) {
        return Jwts.builder()
                .claim("category", category)
                .claim("id", id.toString())
                .claim("email", email)
                .claim("role", "USER")
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 30 * 60 * 1000)) // 만료시간 30분
                .signWith(secretKey)
                .compact();
    }

    // 리프레시 토큰 생성
    public String createRefreshToken(String category, Long id, String email) {
        return Jwts.builder()
                .claim("category", category)
                .claim("id", id.toString())
                .claim("email", email)
                .claim("role", "USER")
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 3 * 60 * 60 * 1000)) // 만료시간 3시간
                .signWith(secretKey)
                .compact();
    }

    // 권한 가져오기
    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parser()
                .clockSkewSeconds(60) // 클라 - 서버 60초 시간차이 허용
                .verifyWith((SecretKey) secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        String userId = claims.get("id", String.class);
        String email = claims.get("email", String.class);
        String role = claims.get("role", String.class);

        // 권한 변환
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role));

        // 유저 아이디와 권한을 기반으로 Authentication 객체 생성
        return new UsernamePasswordAuthenticationToken(userId, null, authorities);
    }

    public String getId(String token) {

        return Jwts.parser()
                .verifyWith((SecretKey) secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("id", String.class);
    }

    public String getEmail(String token) {

        return Jwts.parser()
                .verifyWith((SecretKey) secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("email", String.class);
    }

    public String getCategory(String token) {

        return Jwts.parser()
                .verifyWith((SecretKey) secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("category", String.class);
    }

    // 토큰 파싱 및 서명 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith((SecretKey) secretKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
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
        return false;
    }

    // 토큰 만료 시간 지나면 true
    public Boolean isExpired(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration()
                .before(new Date());
    }

    public Cookie createCookie(String name, String value) {
        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(60*60); // 1시간
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setDomain("jangmadang.site");
//        cookie.setSecure(true); // HTTPS 필수
        return cookie;
    }
}