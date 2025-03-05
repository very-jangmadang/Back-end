package com.example.demo.service.general.impl;

import com.example.demo.base.code.exception.CustomException;
import com.example.demo.base.status.ErrorStatus;
import com.example.demo.domain.converter.LikeConverter;
import com.example.demo.domain.dto.Like.LikeListResponseDTO;
import com.example.demo.domain.dto.Like.LikeResponseDTO;
import com.example.demo.domain.dto.Review.ReviewResponseDTO;
import com.example.demo.entity.Like;
import com.example.demo.entity.Raffle;
import com.example.demo.entity.User;
import com.example.demo.repository.ApplyRepository;
import com.example.demo.repository.LikeRepository;
import com.example.demo.repository.RaffleRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.general.LikeService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {

    private final LikeRepository likeRepository;
    private final RaffleRepository raffleRepository;
    private final UserRepository userRepository;
    private final ApplyRepository applyRepository;

    // 찜하기
    public LikeResponseDTO addLike(Long raffleId, Long userId) {
            // Raffle과 User를 ID로 조회
            Raffle raffle = raffleRepository.findById(raffleId)
                    .orElseThrow(() -> new CustomException(ErrorStatus.RAFFLE_NOT_FOUND));

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));

            if (likeRepository.existsByRaffleAndUser(raffle, user)) {
                throw new CustomException(ErrorStatus.LIKE_ALREADY_FOUND);
            }

            Like like = Like.builder()
                    .raffle(raffle)
                    .user(user)
                    .build();

            likeRepository.save(like);

            // 저장된 Like 객체를 DTO로 변환
            LikeResponseDTO likeResponse = LikeConverter.ToLikeResponseDTO(like);

            return likeResponse;
        }

        // 찜 삭제
        public void deleteLike (Long raffleId, Long userId){

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));

            Like like = likeRepository.findByUserIdAndRaffleId(user.getId(), raffleId)
                        .orElseThrow(() -> new CustomException(ErrorStatus.LIKE_NOT_FOUND));
                likeRepository.delete(like);
            }

            // 찜 목록 조회
            public List<LikeListResponseDTO> getLikedItems (Long userId){

                User user = userRepository.findById(userId)
                        .orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));

                List<Like> likes = likeRepository.findByUserIdOrderByCreatedAtDesc(user.getId());

                // 찜 목록에 포함된 raffleId들 추출
                List<Long> raffleIds = likes.stream()
                        .map(like -> like.getRaffle().getId())
                        .collect(Collectors.toList());

                // 각 raffleId에 대해 응모 횟수 조회
                List<Object[]> applyCounts = applyRepository.countAppliesByRaffleIds(raffleIds);

                Map<Long, Long> applyCountMap = new HashMap<>();
                for (Object[] result : applyCounts) {
                    Long raffleId = (Long) result[0];
                    Long applyCount = (Long) result[1];
                    applyCountMap.put(raffleId, applyCount);
                }

                return likes.stream()
                        .map(like -> {
                            Long raffleId = like.getRaffle().getId();
                            int applyCount = applyCountMap.getOrDefault(raffleId, 0L).intValue(); // 응모 횟수 조회
                            return LikeConverter.toLikeListResponseDTO(like, applyCount); // 응모 횟수 추가
                        })
                        .collect(Collectors.toList());

            }

            //찜 수 조회
            public Long getLikeCount (Long raffleId){

                return likeRepository.countByRaffleId(raffleId);
            }
        }

