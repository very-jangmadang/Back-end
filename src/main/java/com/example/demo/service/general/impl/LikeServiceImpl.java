package com.example.demo.service.general.impl;

import com.example.demo.domain.converter.LikeConverter;
import com.example.demo.domain.dto.Like.LikeListResponseDTO;
import com.example.demo.domain.dto.Like.LikeRequestDTO;
import com.example.demo.domain.dto.Like.LikeResponseDTO;
import com.example.demo.domain.dto.Review.ReviewResponseDTO;
import com.example.demo.entity.Like;
import com.example.demo.entity.Raffle;
import com.example.demo.entity.User;
import com.example.demo.repository.LikeRepository;
import com.example.demo.repository.RaffleRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.general.LikeService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {

    private final LikeRepository likeRepository;
    private final RaffleRepository raffleRepository;
    private final UserRepository userRepository;

    // 찜하기
    public LikeResponseDTO addLike(Long raffleId, LikeRequestDTO likeRequest) {

        // Raffle과 User를 ID로 조회
        Raffle raffle = raffleRepository.findById(raffleId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid raffle ID"));

        User user = userRepository.findById(likeRequest.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));

        // Like 객체 생성
        Like like = new Like(raffle, user);
        likeRepository.save(like);

        // 저장된 Like 객체를 DTO로 변환
        LikeResponseDTO likeResponse = LikeConverter.ToLikeResponseDTO(like);

        return likeResponse;
    }

    // 찜 삭제
    @Override
    public void deleteLike(Long raffleId, Long userId) {
        Like like = likeRepository.findByUserIdAndRaffleId(userId, raffleId)
                .orElseThrow(() -> new EntityNotFoundException("Like not found for raffleId: " + raffleId + " and userId: " + userId));
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

    //찜 수 조회
    public Long getLikeCount(Long raffleId) {

        return likeRepository.countByRaffleId(raffleId);
    }
}
