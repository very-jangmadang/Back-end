package com.example.demo.security.oauth;

import com.example.demo.repository.UserRepository;
import com.example.demo.security.jwt.JWTUtil;
import com.example.demo.service.general.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuthLoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserService userService;
    private final JWTUtil jwtUtil;
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        // 1. 인가 코드 추출
        // 2. 카카오에 토큰 요청
        // 3. 카카오에서 토큰 받아오기
        // 4. 카카오에 사용자 정보 요청

        // 5. 사용자 정보 받아오기
        OidcUser oidcUser = (OidcUser) authentication.getPrincipal();

        // idToken 받아오기
        String idToken = oidcUser.getIdToken().getTokenValue();
        log.info("카카오 ID Token: {}", idToken);

        // 사용자 정보 (이메일 등) 추출
        String email = oidcUser.getEmail();
        log.info("카카오 로그인 이메일: {}", email);

        // 신규 회원
        if (!userService.isExistUserByEmail(email)) {
            HttpSession session = request.getSession();
            log.info("세션아이디 {}", session);
            session.setAttribute("oauthEmail", email);
            log.info("세션값 {}", session.getAttribute("oauthEmail"));
            response.sendRedirect("https://jmd-fe.vercel.app//kakao");
            return;
        }

        // JWT 액세스/리프레시 토큰 생성
        Long userId = userService.findIdByEmail(email);
        String accessToken = jwtUtil.createAccessToken("access", userId, email);
        String refreshToken = jwtUtil.createRefreshToken("refresh", userId, email);

        userService.addRefreshToken(userId, refreshToken);

        log.info("accessToken: {}", accessToken);
        log.info("refreshToken: {}", refreshToken);

        // 쿠키 세팅
        addCookie(response, "idToken", idToken, 3600);
        addCookie(response, "access", accessToken, 3600);
        addCookie(response, "refresh", refreshToken, 3600 * 24 * 7);

        response.sendRedirect("https://jmd-fe.vercel.app");
    }

    private void addCookie(HttpServletResponse response, String name, String value, int maxAgeSeconds) {
        try {
            String encodedValue = URLEncoder.encode(value, StandardCharsets.UTF_8);
            String cookieString = String.format(
                    "%s=%s; Max-Age=%d; Path=/; Domain=.jangmadang.site; SameSite=None; Secure; HttpOnly",
                    name, encodedValue, maxAgeSeconds
            );
            response.addHeader("Set-Cookie", cookieString);
        } catch (Exception e) {
            log.error("쿠키 설정 실패: {}", name, e);
        }
    }
}
