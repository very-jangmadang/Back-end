package com.example.demo.repository;

import com.example.demo.entity.Raffle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RaffleRepository extends JpaRepository<Raffle, Long> {
    Optional<Raffle> findById(Long id);
    List<Raffle> findByCategoryName(String categoryName);

    // 찜 횟수
    @Query("SELECT COUNT(l) FROM Like l WHERE l.raffle.id = :raffleId")
    int countLikeByRaffleId(Long raffleId);

    // 응모 횟수
    @Query("SELECT COUNT(a) FROM Apply a WHERE a.raffle.id = :raffleId")
    int countApplyByRaffleId(Long raffleId);

    // 팔로우 수
    @Query("SELECT COUNT(f) FROM Follow f WHERE f.user.id = :userId")
    int countFollowsByUserId(Long userId);

    // 리뷰 수
    @Query("SELECT COUNT(r) FROM Review r WHERE r.user.id = :userId")
    int countReviewsByUserId(Long userId);

    // 이름으로 래플 검색
    List<Raffle> findAllByNameContaining(String keyword);

    // 주최자로 래플 찾기
    List<Raffle> findAllByUserIdOrderByCreatedAtDesc(Long userId);
}
