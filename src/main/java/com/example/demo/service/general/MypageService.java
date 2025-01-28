package com.example.demo.service.general;

import com.example.demo.domain.dto.MypageResponseDTO;
import com.example.demo.domain.dto.Review.ReviewWithAverageDTO;
import org.springframework.web.multipart.MultipartFile;

public interface MypageService {

    MypageResponseDTO.ApplyListDto getApplies();

    String updateProfileImage(Long userId, MultipartFile file);

    //내 리뷰 조회
    ReviewWithAverageDTO getReviewsByUserId(Long userId);


}
