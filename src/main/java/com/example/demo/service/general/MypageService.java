package com.example.demo.service.general;

import com.example.demo.domain.dto.Mypage.MypageRequestDTO;
import com.example.demo.domain.dto.Mypage.MypageResponseDTO;
import org.springframework.security.core.Authentication;

public interface MypageService {

    MypageResponseDTO.ApplyListDto getApplies();

    MypageResponseDTO.AddressListDto getAddresses(Authentication authentication);

    MypageResponseDTO.AddressListDto setDefault(MypageRequestDTO.AddressDto addressDto, Authentication authentication);

    void addAddress(MypageRequestDTO.AddressAddDto addressAddDto, Authentication authentication);

    void deleteAddress(MypageRequestDTO.AddressDto addressDto, Authentication authentication);
}
