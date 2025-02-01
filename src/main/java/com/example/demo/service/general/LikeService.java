package com.example.demo.service.general;

import com.example.demo.domain.dto.Review.ReviewRequestDTO;
import com.example.demo.domain.dto.Review.ReviewResponseDTO;
import com.example.demo.entity.Like;
import com.example.demo.entity.Raffle;
import com.example.demo.repository.LikeRepository;
import com.example.demo.domain.dto.Like.LikeListResponseDTO;
import com.example.demo.domain.dto.Like.LikeResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

public interface LikeService {

    // 찜 생성 작성
    LikeResponseDTO addLike(Long raffleId, Long uesrId);

    // 리뷰 삭제
    void deleteLike(Long raffleId, Long userId);

    //찜 목록 조회
    List<LikeListResponseDTO> getLikedItems(Long userId);

    //찜 수 조회
    Long getLikeCount(Long raffleId);

}
