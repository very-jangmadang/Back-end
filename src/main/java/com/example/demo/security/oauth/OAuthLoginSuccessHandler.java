package com.example.demo.security.oauth;

import com.example.demo.security.jwt.JWTUtil;
import com.example.demo.service.general.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuthLoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserService userService;
    private final JWTUtil jwtUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        // 1. 인가 코드 추출
        // 2. 카카오에 토큰 요청
        // 3. 카카오에서 토큰 받아오기
        // 4. 카카오에 사용자 정보 요청

        // 5. 사용자 정보 받아오기
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        log.info("로그인 성공! 사용자 정보: {}", oAuth2User.getAttributes());

        // 6. 사용자 정보 확인
        Map<String, Object> kakaoAccount = (Map<String, Object>) oAuth2User.getAttributes().get("kakao_account");
        String email = kakaoAccount.get("email").toString();
        log.info("{}", kakaoAccount.get("email"));

        boolean isNewUser = !userService.isExistUser(email);

        // 기존 회원이 아닌경우
        if (!userService.isExistUser(email)) {
            userService.createUser(email);
        }

        // 엑세스 토큰 생성
        Long userId = userService.findIdByEmail(email);
        String accessToken = jwtUtil.createAccessToken(userId, email);

        response.addCookie(createCookie("Authorization", accessToken)); // 쿠키로 전달하기

        // 6. JSON 응답 (리다이렉트 없이 직접 Body 전송)
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json;charset=UTF-8");

        String jsonResponse = String.format(
                "{\"accessToken\":\"%s\", \"isNewUser\":%b}",
                accessToken, isNewUser
        );
        response.getWriter().write(jsonResponse);
    }

    private Cookie createCookie(String name, String value) {
        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(60*60); // 1시간
        cookie.setPath("/");
        cookie.setHttpOnly(true);
//        cookie.setSecure(true); // HTTPS 필수
        return cookie;
    }
}