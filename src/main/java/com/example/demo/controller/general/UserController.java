package com.example.demo.controller.general;

import com.example.demo.base.ApiResponse;
import com.example.demo.base.status.ErrorStatus;
import com.example.demo.base.status.SuccessStatus;
import com.example.demo.domain.dto.User.UserRequestDTO;
import com.example.demo.domain.dto.User.UserResponseDTO;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.jwt.JWTUtil;
import com.example.demo.service.general.UserService;
import com.example.demo.service.general.impl.UserServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final JWTUtil jwtUtil;
    private final UserServiceImpl userServiceImpl;

    @Operation(summary = "로그인 확인")
    @GetMapping("api/permit/user-info")
    public ApiResponse<String> isLogin() {
        String result = userService.isLogin();

        return ApiResponse.of(SuccessStatus._OK, result);
    }

    @Operation(summary = "사용자 정보 확인, 사업자 여부 프론트에서 알기 위한 api")
    @GetMapping("api/permit/me")
    public ApiResponse<?> Me() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ApiResponse.onFailure(ErrorStatus.COMMON_UNAUTHORIZED, null);
        }

        Long userId = Long.parseLong(authentication.getName());
        User user = userRepository.findById(userId).orElseThrow();

        UserResponseDTO.MeDTO dto = UserResponseDTO.MeDTO.builder()
                .email(user.getEmail())
                .nickname(user.getNickname())
                .isBusiness(Boolean.TRUE.equals(user.getIsBusiness()))
                .build();

        return ApiResponse.of(SuccessStatus._OK, dto);
    }

    @Operation(summary = "프론트에게 idToken 반환하는 1회성 api")
    @GetMapping("/api/permit/idtoken")
    public ApiResponse<?> getIdToken(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return ApiResponse.onFailure(ErrorStatus.USER_WITHOUT_SESSION, null);

        String idToken = (String) session.getAttribute("oidcIdToken");
        if (idToken == null) return ApiResponse.onFailure(ErrorStatus.TOKEN_NOT_FOUND, null);

        session.removeAttribute("oidcIdToken"); // 1회성

        return ApiResponse.of(SuccessStatus._OK, idToken);
    }

    @Operation(summary = "로그아웃")
    @PostMapping("api/permit/logout")
    public ApiResponse<?> logout(HttpServletResponse response) {
        Cookie cookie1 = jwtUtil.createCookie("access", null, 0);
        Cookie cookie2 = jwtUtil.createCookie("refresh", null, 0);
        Cookie cookie3 = jwtUtil.createCookie("idToken", null, 0);

        response.addCookie(cookie1);
        response.addCookie(cookie2);
        response.addCookie(cookie3);

        log.info("브라우저 쿠키 삭제 완료");

        return ApiResponse.of(SuccessStatus.USER_LOGOUT_SUCCESS, null);
    }

    // 닉네임 입력
    @Operation(summary = "닉네임 입력")
    @PostMapping("api/permit/nickname")
    public ApiResponse<UserResponseDTO.SignUpResponseDTO> saveNickname(HttpServletRequest httpServletRequestequest,
                                                                       HttpServletResponse httpServletResponse,
                                                                       @Valid @RequestBody UserRequestDTO.nicknameDTO request) {
        HttpSession session = httpServletRequestequest.getSession(false); // 세션이 없으면 null 반환

        if (session == null) {
            log.warn("세션이 존재하지 않음");
            return ApiResponse.onFailure(
                    ErrorStatus.USER_WITHOUT_SESSION, null);
        }

        String email = session.getAttribute("oauthEmail").toString();

        if (email == null) {
            log.warn("[닉네임 입력] 세션 있음 - 하지만 oauthEmail 없음. 세션 ID: {}", session.getId());
            return ApiResponse.onFailure(ErrorStatus.USER_WITHOUT_OAUTHEMAIL, null);
        }

        Boolean isBusiness = (Boolean) session.getAttribute("is_business");
        String businessCode = (String) session.getAttribute("business_code");

        if (isBusiness == null) {
            // 사업자/일반 선택 단계 없이 넘어온 케이스 방지
            isBusiness = Boolean.FALSE;
        }

        String nickname = request.getNickname();
        userService.createUser(nickname, email, isBusiness, businessCode);


        Long userId = userService.findIdByEmail(email);
        String accessToken = jwtUtil.createAccessToken("access", userId, email);
        String refreshToken = jwtUtil.createRefreshToken("refresh", userId, email);

        userService.addRefreshToken(userId, refreshToken); // 리프레시 토큰 저장

        httpServletResponse.addCookie(jwtUtil.createCookie("access", accessToken, 24 * 60 * 60)); // 24시간(개발용)
        httpServletResponse.addCookie(jwtUtil.createCookie("refresh", refreshToken, 7 * 24 * 60 * 60)); // 1주일(개발용)

        session.removeAttribute("oauthEmail");
        session.removeAttribute("is_business");
        session.removeAttribute("business_code");

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

    // 사업자 등록번호, 사업자 여부 요청받아서 세션에 저장 -> 이후 닉네임 입력받는 api로 이어짐
    @Operation(summary = "사업자 등록번호, 사업자 여부 입력받아서 세션에 저장, 이후 닉네임 입력받는 api로 이어짐")
    @PostMapping("api/permit/business")
    public ApiResponse<Void> registerBusiness(HttpServletRequest httpServletRequest,
                                           @Valid @RequestBody UserRequestDTO.businessCodeDTO request) {
        HttpSession session = httpServletRequest.getSession();
        userService.registerBusiness(session, request);

        return ApiResponse.of(SuccessStatus._OK, null);
    }
}