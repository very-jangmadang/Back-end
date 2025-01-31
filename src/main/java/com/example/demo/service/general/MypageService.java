package com.example.demo.service.general;

import com.example.demo.domain.dto.Mypage.MypageRequestDTO;
import com.example.demo.domain.dto.Mypage.MypageResponseDTO;

public interface MypageService {

    MypageResponseDTO.ApplyListDto getApplies();

    MypageResponseDTO.AddressListDto getAddresses(Long userId);

    MypageResponseDTO.AddressListDto setDefault(MypageRequestDTO.AddressDto addressDto, Long userId);

    void addAddress(MypageRequestDTO.AddressAddDto addressAddDto, Long userId);

    void deleteAddress(MypageRequestDTO.AddressDto addressDto, Long userId);
}
