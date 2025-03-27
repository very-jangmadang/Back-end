package com.example.demo.controller.general;

import com.example.demo.base.ApiResponse;
import com.example.demo.base.status.SuccessStatus;
import com.example.demo.domain.dto.Home.HomeRaffleListDTO;
import com.example.demo.domain.dto.Home.HomeResponseDTO;
import com.example.demo.service.general.HomeService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/member/home")
@RequiredArgsConstructor
public class HomeLoginController {

    private final HomeService homeService;

    @Operation(summary = "홈 화면 조회")
    @GetMapping("")
    public ApiResponse<HomeResponseDTO> home(Authentication authentication,
                                             @RequestParam(defaultValue = "1") int page,
                                             @RequestParam(defaultValue = "16") int size){
        Long userId = Long.parseLong(authentication.getName());
        HomeResponseDTO result =  homeService.getHomeLogin(userId, page, size);
        return ApiResponse.of(SuccessStatus._OK, result);
    }

    @Operation(summary = "카테고리별 래플 조회")
    @GetMapping("/categories")
    public ApiResponse<HomeRaffleListDTO> homeCategories(@RequestParam("categoryName") String categoryName,
                                                         @RequestParam(defaultValue = "1") int page,
                                                         @RequestParam(defaultValue = "16") int size,
                                                         Authentication authentication){
        Long userId = Long.parseLong(authentication.getName());
        HomeRaffleListDTO result =  homeService.getHomeCategoriesLogin(categoryName, userId, page, size);
        return ApiResponse.of(SuccessStatus._OK, result);
    }

    @Operation(summary = "마감임박 상품 더보기")
    @GetMapping("/approaching")
    public ApiResponse<HomeRaffleListDTO> homeApproaching(Authentication authentication,
                                                          @RequestParam(defaultValue = "1") int page,
                                                          @RequestParam(defaultValue = "16") int size){
        Long userId = Long.parseLong(authentication.getName());
        HomeRaffleListDTO result =  homeService.getHomeApproachingLogin(userId, page, size);
        return ApiResponse.of(SuccessStatus._OK, result);
    }

    @Operation(summary = "팔로우한 상점의 래플 더보기")
    @GetMapping("/following")
    public ApiResponse<HomeRaffleListDTO> homeFollowingRaffles(Authentication authentication,
                                                               @RequestParam(defaultValue = "1") int page,
                                                               @RequestParam(defaultValue = "16") int size){
        Long userId = Long.parseLong(authentication.getName());
        HomeRaffleListDTO result = homeService.getHomeFollowingRaffles(userId, page, size);
        return ApiResponse.of(SuccessStatus._OK, result);
    }

    @Operation(summary = "래플 둘러보기")
    @GetMapping("/more")
    public ApiResponse<HomeRaffleListDTO> homeMoreRaffles(Authentication authentication,
                                                          @RequestParam(defaultValue = "1") int page,
                                                          @RequestParam(defaultValue = "16") int size){
        Long userId = Long.parseLong(authentication.getName());
        HomeRaffleListDTO result =  homeService.getHomeMoreRafflesLogin(userId, page, size);
        return ApiResponse.of(SuccessStatus._OK, result);
    }

    @Operation(summary = "내가 찜한 래플 더보기")
    @GetMapping("/likes")
    public ApiResponse<HomeRaffleListDTO> homeLikeRaffles(Authentication authentication,
                                                          @RequestParam(defaultValue = "1") int page,
                                                          @RequestParam(defaultValue = "16") int size){
        Long userId = Long.parseLong(authentication.getName());
        HomeRaffleListDTO result = homeService.getHomeLikeRaffles(userId, page, size);
        return ApiResponse.of(SuccessStatus._OK, result);
    }

}
