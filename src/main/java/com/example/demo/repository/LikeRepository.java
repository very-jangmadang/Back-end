package com.example.demo.repository;

import com.example.demo.entity.Like;
import com.example.demo.entity.Raffle;
import com.example.demo.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {

    // 특정 사용자가 찜한 raffleId 목록 조회
    List<Like> findByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<Like> findByUserIdAndRaffleId(Long userId, Long raffleId);

    Long countByRaffleId (Long raffleId);

    boolean existsByRaffleAndUser(Raffle raffle, User user);

    @Query("SELECT l.raffle.id, COUNT(l) > 0 " +
            "FROM Like l WHERE l.raffle.id IN :raffleIds AND l.user = :user GROUP BY l.raffle.id")
    List<Object[]> checkLikesByRaffleIdsAndUser(@Param("raffleIds") List<Long> raffleIds, @Param("user") User user);

    @Query("SELECT l FROM Like l JOIN FETCH l.user WHERE l.raffle = :raffle")
    List<Like> findByRaffleWithUser(@Param("raffle") Raffle raffle);

    @Query("SELECT l.raffle FROM Like l WHERE l.user.id = :userId ORDER BY l.createdAt DESC")
    Page<Raffle> findRaffleByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId, Pageable pageable);

    // 래플 리스트에서 유저가 좋아요 했는지 여부를 전송
    @Query("SELECT l.raffle.id FROM Like l WHERE l.raffle.id IN :raffleIds AND l.user = :user")
    List<Long> findLikedRaffleIdsByUserAndRaffleList(@Param("raffleIds") List<Long> raffleIds, @Param("user") User user);
}
