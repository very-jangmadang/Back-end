package com.example.demo.controller;

import com.example.demo.base.ApiResponse;
import com.example.demo.domain.dto.HomeResponseDTO;
import com.example.demo.service.HomeService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.example.demo.base.status.SuccessStatus._OK;

@RestController
@RequestMapping("/home")
@RequiredArgsConstructor
public class HomeController {

    private final HomeService homeService;

    @Operation(summary = "홈 화면 조회")
    @GetMapping("")
    public ApiResponse<HomeResponseDTO> home(){
        HomeResponseDTO result = homeService.getHome();
        return ApiResponse.of(_OK, result);
    }

}
