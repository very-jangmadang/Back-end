package com.example.demo.repository;

import com.example.demo.entity.Like;
import com.example.demo.entity.Raffle;
import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {

    // 특정 사용자가 찜한 raffleId 목록 조회
    List<Like> findByUserId(Long userId);

    Optional<Like> findByUserIdAndRaffleId(Long userId, Long raffleId);

    Long countByRaffleId (Long raffleId);

    boolean existsByRaffleAndUser(Raffle raffle, User user);

}
