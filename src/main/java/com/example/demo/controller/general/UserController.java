package com.example.demo.controller.general;

import com.example.demo.base.ApiResponse;
import com.example.demo.base.status.ErrorStatus;
import com.example.demo.base.status.SuccessStatus;
import com.example.demo.domain.dto.User.UserRequestDTO;
import com.example.demo.domain.dto.User.UserResponseDTO;
import com.example.demo.entity.User;
import com.example.demo.security.jwt.JWTUtil;
import com.example.demo.service.general.UserService;
import com.example.demo.service.handler.NicknameGenerator;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JWTUtil jwtUtil;

    @Operation(summary = "로그인 확인")
    @GetMapping("api/permit/user-info")
    public ApiResponse<String> isLogin() {
        String result = userService.isLogin();

        return ApiResponse.of(SuccessStatus._OK, result);
    }

    @Operation(summary = "로그아웃")
    @PostMapping("api/permit/logout")
    public ApiResponse<?> logout(HttpServletResponse response) {
        Cookie cookie1 = jwtUtil.createCookie("access", null, 0);
        Cookie cookie2 = jwtUtil.createCookie("refresh", null, 0);

        response.addCookie(cookie1);
        response.addCookie(cookie2);
        log.info("브라우저 쿠키 삭제 완료");

        return ApiResponse.of(SuccessStatus.USER_LOGOUT_SUCCESS, null);
    }

    // 닉네임 입력
    @Operation(summary = "닉네임 입력")
    @PostMapping("api/permit/nickname")
    public ApiResponse<UserResponseDTO.SignUpResponseDTO> saveNickname(HttpServletRequest httpServletRequestequest,
                                                                       HttpServletResponse httpServletResponse,
                                                                       @Valid @RequestBody UserRequestDTO.nicknameDTO request) {

        if(httpServletRequestequest.getSession().getAttribute("oauthEmail") == null){
            log.info("세션아이디 {}",httpServletRequestequest.getSession());
        }

        String email = httpServletRequestequest.getSession().getAttribute("oauthEmail").toString();
        String nickname = request.getNickname();
        userService.createUser(nickname, email);

        Long userId = userService.findIdByEmail(email);
        String accessToken = jwtUtil.createAccessToken("access", userId, email);
        String refreshToken = jwtUtil.createRefreshToken("refresh", userId, email);

        userService.addRefreshToken(userId, refreshToken); // 리프레시 토큰 저장

        httpServletResponse.addCookie(jwtUtil.createCookie("access", accessToken, 60 * 60)); // 쿠키로 전달
        httpServletResponse.addCookie(jwtUtil.createCookie("refresh", refreshToken, 3 * 60 * 60)); // 쿠키로 전달

        return ApiResponse.of(SuccessStatus._OK, null);
    }

    @Operation(summary = "회원가입 취소")
    @PostMapping("api/permit/back")
    public ApiResponse<?> cancel(HttpServletRequest httpServletRequest) {
        httpServletRequest.getSession().invalidate();
        log.info("세션 삭제 확인 {}", httpServletRequest.getSession().getAttribute("oauthEmail"));
        log.info("세션 확인 {}", httpServletRequest.getSession().getAttributeNames());

        return ApiResponse.of(SuccessStatus._OK, null);
    }

    // 랜덤 닉네임 부여
    @Operation(summary = "닉네임 부여")
    @GetMapping("api/permit/nickname")
    public ApiResponse<UserResponseDTO.SignUpResponseDTO> randomNickname() {
        UserResponseDTO.SignUpResponseDTO result = userService.randomNickname();

        return ApiResponse.of(SuccessStatus._OK, result);
    }
}