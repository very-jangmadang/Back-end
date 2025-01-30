package com.example.demo.service.general;

import com.example.demo.domain.dto.Mypage.MypageRequestDTO;
import com.example.demo.domain.dto.Mypage.MypageResponseDTO;

public interface MypageService {

    MypageResponseDTO.ApplyListDto getApplies();

    MypageResponseDTO.AddressListDto getAddresses();

    MypageResponseDTO.AddressListDto setDefault(MypageRequestDTO.AddressDto addressDto);

    void addAddress(MypageRequestDTO.AddressAddDto addressAddDto);
}
