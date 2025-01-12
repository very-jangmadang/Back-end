package com.example.demo.service.general;

import com.example.demo.entity.Like;
import com.example.demo.entity.Raffle;
import com.example.demo.repository.LikeRepository;
import com.example.demo.domain.dto.Like.LikeListResponseDTO;
import com.example.demo.domain.dto.Like.LikeRequestDTO;
import com.example.demo.domain.dto.Like.LikeResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;

    // 찜하기
    @Transactional
    public LikeResponseDTO addLike(Long raffleId, LikeRequestDTO likeRequest) {
        Long userId = likeRequest.getUserId();

        LikeResponseDTO likeResponse = new LikeResponseDTO(1L, raffleId, userId);

        return likeResponse;
    }

    // 찜 삭제
    @Transactional
    public void deleteLike(Long raffleId, Long userId) {
        Like like = likeRepository.findByUserIdAndRaffleId(userId, raffleId)
                .orElseThrow(() -> new IllegalArgumentException("찜하지 않은 항목입니다."));

        likeRepository.delete(like);
    }

    // 찜 목록 조회
    @Transactional(readOnly = true)
    public List<LikeListResponseDTO> getLikedItems(Long userId) {
        List<Like> likes = likeRepository.findByUserId(userId);

        return likes.stream()
                .map(like -> {
                    Raffle raffle = like.getRaffle();
                    return new LikeListResponseDTO(
                            like.getId(),            // likeId
                            raffle.getId(),          // raffleId
                            raffle.getName(),        // productName
                            like.getUser().getId()   // userId
                    );
                }).collect(Collectors.toList());
    }
}
