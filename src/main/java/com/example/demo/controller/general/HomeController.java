package com.example.demo.controller.general;

import com.example.demo.base.ApiResponse;
import com.example.demo.base.status.SuccessStatus;
import com.example.demo.domain.dto.HomeResponseDTO;
import com.example.demo.service.general.HomeService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/permit/home")
@RequiredArgsConstructor
public class HomeController {

    private final HomeService homeService;

    @Operation(summary = "홈 화면 조회")
    @GetMapping("")
    public ApiResponse<HomeResponseDTO> home(){
        HomeResponseDTO result = homeService.getHome();
        return ApiResponse.of(SuccessStatus._OK, result);
    }

    @Operation(summary = "카테고리별 래플 조회")
    @GetMapping("/categories")
    public ApiResponse<HomeResponseDTO> homeCategories(@RequestParam("categoryName") String categoryName){
        HomeResponseDTO result = homeService.getHomeCategories(categoryName);
        return ApiResponse.of(SuccessStatus._OK, result);
    }

}
