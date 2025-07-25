package com.example.demo.service.general;

import com.example.demo.domain.dto.Mypage.MypageRequestDTO;
import com.example.demo.domain.dto.Mypage.MypageResponseDTO;
import com.example.demo.domain.dto.Review.ReviewWithAverageDTO;
import org.springframework.web.multipart.MultipartFile;


public interface MypageService {

    String updateProfileImage(Long userId, MultipartFile file);

    //내 리뷰 조회
    ReviewWithAverageDTO getMyReviewsByUserId(Long userId);

    boolean updateFollowerVisibility(Long userId, boolean isVisible);
    boolean getFollowerVisibility(Long userId);

    String changeNickname(Long userId, String nickname);

    MypageResponseDTO.AddressListDto getAddresses();

    MypageResponseDTO.AddressListDto setDefault(MypageRequestDTO.AddressDto addressDto);

    void addAddress(MypageRequestDTO.AddressAddDto addressAddDto);

    void deleteAddress(MypageRequestDTO.AddressDeleteDto addressDeleteDto);

    MypageResponseDTO.MyPageInfoDto getMyPageMyApplyRaffles(Long userId);

    MypageResponseDTO.MyPageInfoDto getMyPageMyHostRaffles(Long userId);

    MypageResponseDTO.ProfileInfoDto getProfileHostRaffles(Long userId);

    MypageResponseDTO.ProfileInfoWithReviewsDto getProfileReviews(Long userId);

    MypageResponseDTO.CheckBusinessDto checkBusiness();
}
