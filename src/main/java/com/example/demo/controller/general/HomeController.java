package com.example.demo.controller.general;

import com.example.demo.base.ApiResponse;
import com.example.demo.base.status.ErrorStatus;
import com.example.demo.base.status.SuccessStatus;
import com.example.demo.domain.dto.Home.HomeRaffleListDTO;
import com.example.demo.domain.dto.Home.HomeResponseDTO;
import com.example.demo.service.general.HomeService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/permit/home")
@RequiredArgsConstructor
public class HomeController {

    private final HomeService homeService;

    @Operation(summary = "홈 화면 조회")
    @GetMapping("")
    public ApiResponse<HomeResponseDTO> home(Authentication authentication){

        // 로그인 안한 회원인 경우
        if (authentication == null || !authentication.isAuthenticated()) {
            HomeResponseDTO result = homeService.getHome();
            return ApiResponse.of(SuccessStatus._OK, result);
        }

        // 로그인 한 회원인 경우
        else{
            Long userId = Long.parseLong(authentication.getName());
            HomeResponseDTO result =  homeService.getHomeLogin(userId);
            return ApiResponse.of(SuccessStatus._OK, result);
        }

    }

    @Operation(summary = "카테고리별 래플 조회")
    @GetMapping("/categories")
    public ApiResponse<HomeRaffleListDTO> homeCategories(@RequestParam("categoryName") String categoryName, Authentication authentication){

        // 로그인 안한 회원인 경우
        if (authentication == null || !authentication.isAuthenticated()) {
            HomeRaffleListDTO result = homeService.getHomeCategories(categoryName);
            return ApiResponse.of(SuccessStatus._OK, result);
        }

        // 로그인 한 회원인 경우
        else{
            Long userId = Long.parseLong(authentication.getName());
            HomeRaffleListDTO result =  homeService.getHomeCategoriesLogin(categoryName, userId);
            return ApiResponse.of(SuccessStatus._OK, result);
        }

    }

    @Operation(summary = "마감임박 상품 더보기")
    @GetMapping("/approaching")
    public ApiResponse<HomeRaffleListDTO> homeApproaching(Authentication authentication){

        // 로그인 안한 회원인 경우
        if (authentication == null || !authentication.isAuthenticated()) {
            HomeRaffleListDTO result = homeService.getHomeApproaching();
            return ApiResponse.of(SuccessStatus._OK, result);
        }

        // 로그인 한 회원인 경우
        else{
            Long userId = Long.parseLong(authentication.getName());
            HomeRaffleListDTO result =  homeService.getHomeApproachingLogin(userId);
            return ApiResponse.of(SuccessStatus._OK, result);
        }
    }

    @Operation(summary = "팔로우한 상점의 래플 더보기")
    @GetMapping("/following")
    public ApiResponse<HomeRaffleListDTO> homeFollowingRaffles(Authentication authentication){

        if (authentication == null || !authentication.isAuthenticated()) {
            return ApiResponse.onFailure(ErrorStatus.COMMON_UNAUTHORIZED, null);
        }

        Long userId = Long.parseLong(authentication.getName());
        HomeRaffleListDTO result = homeService.getHomeFollowingRaffles(userId);
        return ApiResponse.of(SuccessStatus._OK, result);
    }




}
