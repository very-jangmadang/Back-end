package com.example.demo.controller.general;

import com.example.demo.base.ApiResponse;
import com.example.demo.base.status.SuccessStatus;
import com.example.demo.domain.dto.Home.HomeRaffleListDTO;
import com.example.demo.domain.dto.Home.HomeResponseDTO;
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
    public ApiResponse<HomeResponseDTO> home(@RequestParam(defaultValue = "1") int page,
                                             @RequestParam(defaultValue = "16") int size){
        HomeResponseDTO result = homeService.getHome(page, size);
        return ApiResponse.of(SuccessStatus._OK, result);
    }

    @Operation(summary = "카테고리별 래플 조회")
    @GetMapping("/categories")
    public ApiResponse<HomeRaffleListDTO> homeCategories(@RequestParam("categoryName") String categoryName,
                                                         @RequestParam(defaultValue = "1") int page,
                                                         @RequestParam(defaultValue = "16") int size){
        HomeRaffleListDTO result = homeService.getHomeCategories(categoryName, page, size);
        return ApiResponse.of(SuccessStatus._OK, result);
    }

    @Operation(summary = "마감임박 상품 더보기")
    @GetMapping("/approaching")
    public ApiResponse<HomeRaffleListDTO> homeApproaching(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "16") int size
    ){
        HomeRaffleListDTO result = homeService.getHomeApproaching(page, size);
        return ApiResponse.of(SuccessStatus._OK, result);
    }


    @Operation(summary = "래플 둘러보기")
    @GetMapping("/more")
    public ApiResponse<HomeRaffleListDTO> homeMoreRaffles(@RequestParam(defaultValue = "1") int page,
                                                          @RequestParam(defaultValue = "16") int size){
        HomeRaffleListDTO result = homeService.getHomeMoreRaffles(page, size);
        return ApiResponse.of(SuccessStatus._OK, result);
    }


}
