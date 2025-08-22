package com.example.demo.repository;

import com.example.demo.entity.Payment.TopUp;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface TopUpRepository extends JpaRepository<TopUp, Long> {
    Optional<TopUp> findByTxId(String txId);

    Page<TopUp> findByUserIdAndConfirmedAtAfterOrderByConfirmedAtDesc(
            Long userId, LocalDateTime confirmedAfter, Pageable pageable
    );
}