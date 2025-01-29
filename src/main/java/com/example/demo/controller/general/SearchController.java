package com.example.demo.controller.general;

import com.example.demo.base.ApiResponse;
import com.example.demo.base.status.SuccessStatus;
import com.example.demo.domain.dto.Home.HomeRaffleListDTO;
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

    @Operation(summary = "검색")
    @GetMapping("/raffles")
    public ApiResponse<HomeRaffleListDTO> searchRaffles(@RequestParam String keyword, Authentication authentication){
        Long userId = null;

        // 로그인 한 경우
        if(authentication != null && authentication.isAuthenticated()){
            userId = Long.parseLong(authentication.getName());
        }

        HomeRaffleListDTO result = searchService.searchRaffles(keyword, userId);
        return ApiResponse.of(SuccessStatus._OK, result);
    }


}
