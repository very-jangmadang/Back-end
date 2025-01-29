package com.example.demo.service.general;

import com.example.demo.domain.dto.MypageResponseDTO;
import com.example.demo.domain.dto.Review.ReviewWithAverageDTO;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

public interface MypageService {

    MypageResponseDTO.ApplyListDto getApplies();

    String updateProfileImage(Authentication authentication, MultipartFile file);

    //내 리뷰 조회
    ReviewWithAverageDTO getMyReviewsByUserId(Authentication authentication);


}
