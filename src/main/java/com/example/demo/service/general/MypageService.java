package com.example.demo.service.general;

import com.example.demo.domain.dto.MypageResponseDTO;
import org.springframework.web.multipart.MultipartFile;

public interface MypageService {

    MypageResponseDTO.ApplyListDto getApplies();

    String updateProfileImage(Long userId, MultipartFile file);
}
