package com.example.demo.controller.general;

import com.example.demo.base.ApiResponse;
import com.example.demo.base.status.SuccessStatus;
import com.example.demo.domain.dto.Mypage.MypageRequestDTO;
import com.example.demo.domain.dto.Mypage.MypageResponseDTO;
import com.example.demo.service.general.MypageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/permit/mypage")
@RequiredArgsConstructor
public class MypageController {

    private final MypageService mypageService;

    @GetMapping("/applies")
    public ApiResponse<MypageResponseDTO.ApplyListDto> getApplies(){

        return ApiResponse.of(SuccessStatus._OK, mypageService.getApplies());

    }

    @GetMapping("/setting/addresses")
    public ApiResponse<MypageResponseDTO.AddressListDto> getAddresses(Authentication authentication){

        return ApiResponse.of(SuccessStatus._OK, mypageService.getAddresses(authentication));
    }

    @PostMapping("/setting/addresses")
    public ApiResponse<MypageResponseDTO.AddressListDto> setDefault(
            @RequestBody MypageRequestDTO.AddressDto addressDto, Authentication authentication){

        return ApiResponse.of(SuccessStatus._OK, mypageService.setDefault(addressDto, authentication));
    }

    @PostMapping("/setting/addresses/add")
    public ApiResponse<?> addAddress(
            @RequestBody MypageRequestDTO.AddressAddDto addressAddDto, Authentication authentication){

        mypageService.addAddress(addressAddDto, authentication);

        return ApiResponse.of(SuccessStatus._OK, null);
    }

    @DeleteMapping("/setting/addresses")
    public ApiResponse<?> deleteAddress(
            @RequestBody MypageRequestDTO.AddressDto addressDto, Authentication authentication){

        mypageService.deleteAddress(addressDto, authentication);

        return ApiResponse.of(SuccessStatus._OK, null);


    }
}
