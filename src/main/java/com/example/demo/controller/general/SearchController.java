package com.example.demo.controller.general;

import com.example.demo.base.ApiResponse;
import com.example.demo.base.status.SuccessStatus;
import com.example.demo.domain.dto.Home.HomeRaffleListDTO;
import com.example.demo.domain.dto.Search.SearchResponseDTO;
import com.example.demo.service.general.SearchService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/permit/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @Operation(summary = "최근검색어와 인기검색어 조회")
    @GetMapping("")
    public ApiResponse<SearchResponseDTO> getRecentPopularSearch(Authentication authentication){

        // 로그인 안한 경우, 최근 검색어와 인기검색어 없이 result에 null 반환
        if(authentication == null || !authentication.isAuthenticated()){
            return ApiResponse.of(SuccessStatus._OK, null);
        }

        // 로그인 한 경우, 유저아이디를 통해 최근 검색어와 인기검색어 조회
        else{
            Long userId = Long.parseLong(authentication.getName());
            SearchResponseDTO result = searchService.getRecentPopularSearch(userId);
            return ApiResponse.of(SuccessStatus._OK, result);
        }

    }

    @Operation(summary = "검색")
    @GetMapping("/raffles")
    public ApiResponse<HomeRaffleListDTO> searchRaffles(@RequestParam("keyword") String keyword, Authentication authentication){

        // 로그인 안 했을 경우 유저아이디 null로 처리
        Long userId = null;

        // 로그인 한 경우
        if(authentication != null && authentication.isAuthenticated()){
            userId = Long.parseLong(authentication.getName());
        }

        HomeRaffleListDTO result = searchService.searchRaffles(keyword, userId);
        return ApiResponse.of(SuccessStatus._OK, result);
    }


}
