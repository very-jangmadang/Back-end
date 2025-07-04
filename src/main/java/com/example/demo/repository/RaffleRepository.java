package com.example.demo.repository;

import com.example.demo.entity.Raffle;
import com.example.demo.entity.base.enums.RaffleStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.userdetails.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RaffleRepository extends JpaRepository<Raffle, Long> {

    @Query("SELECT r FROM Raffle r " +
            "LEFT JOIN Apply a ON r.id = a.raffle.id " +
            "WHERE r.category.name = :categoryName " +
            "GROUP BY r " +
            "ORDER BY COUNT(a) DESC")
    Page<Raffle> findByCategoryNameSortedByApply(@Param("categoryName") String categoryName, Pageable pageable);

    // 찜 횟수
    @Query("SELECT COUNT(l) FROM Like l WHERE l.raffle.id = :raffleId")
    int countLikeByRaffleId(Long raffleId);

    // 응모 횟수
    @Query("SELECT COUNT(a) FROM Apply a WHERE a.raffle.id = :raffleId")
    int countApplyByRaffleId(Long raffleId);

    // 팔로우 수
    @Query("SELECT COUNT(f) FROM Follow f WHERE f.storeId = :userId")
    int countFollowsByUserId(Long userId);

    // 리뷰 수
    @Query("SELECT COUNT(r) FROM Review r WHERE r.user.id = :userId")
    int countReviewsByUserId(Long userId);

    // 이름으로 래플 검색
    Page<Raffle> findAllByNameContaining(String keyword, Pageable pageable);

    // 주최자로 래플 찾기
    List<Raffle> findAllByUserIdOrderByCreatedAtDesc(Long userId);

    List<Raffle> findAllByUserId(Long userId);

    List<Raffle> findByRaffleStatusIn(List<RaffleStatus> statuses);

    List<Raffle> findByIdGreaterThanEqualOrderByIdAsc(Long raffleId);

    // 마감임박순 래플 조회(24시간 이내에 마감인것들에서 마감임박순으로)
    @Query("SELECT r FROM Raffle r WHERE r.endAt BETWEEN :now AND :maxTime ORDER BY r.endAt ASC")
    Page<Raffle> findRafflesEndingSoon(@Param("now") LocalDateTime now, @Param("maxTime") LocalDateTime maxTime, Pageable pageable);

    Page<Raffle> findByEndAtBetweenOrderByEndAtAsc(LocalDateTime now, LocalDateTime maxTime, Pageable pageable);

    // 응모자순 래플 조회(응모가 안마감된 것들 우선으로)
    @Query(
            value = "SELECT r FROM Raffle r " +
                    "LEFT JOIN Apply a ON r.id = a.raffle.id " +
                    "GROUP BY r " +
                    "ORDER BY " +
                    "CASE WHEN r.endAt > :now THEN 1 ELSE 2 END, " +
                    "COUNT(a) DESC",
            countQuery = "SELECT COUNT(DISTINCT r) FROM Raffle r"
    )
    Page<Raffle> findAllSortedByApply(@Param("now") LocalDateTime now, Pageable pageable);

    @Query("SELECT r FROM Raffle r WHERE r.id = :id")
    Optional<Raffle> findByIdIncludeDeleted(@Param("id") Long id);


}


