package com.example.demo.service.general;

import com.example.demo.domain.dto.Mypage.MypageRequestDTO;
import com.example.demo.domain.dto.Mypage.MypageResponseDTO;
import com.example.demo.domain.dto.Mypage.MypageResponseDTO;
import com.example.demo.domain.dto.Review.ReviewWithAverageDTO;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;


public interface MypageService {

    MypageResponseDTO.ApplyListDto getApplies();

    String updateProfileImage(Authentication authentication, MultipartFile file);

    //내 리뷰 조회
    ReviewWithAverageDTO getMyReviewsByUserId(Authentication authentication);

    MypageResponseDTO.AddressListDto getAddresses();

    MypageResponseDTO.AddressListDto setDefault(MypageRequestDTO.AddressDto addressDto);

    void addAddress(MypageRequestDTO.AddressAddDto addressAddDto);

}
