package com.example.demo.repository;

import com.example.demo.entity.Payment.Exchange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface ExchangeRepository extends JpaRepository<Exchange, Long> {

    // 사용자 ID로 환전 내역을 조회하고 환전시간 기준 내림차순 정렬하여 페이징 처리
    Page<Exchange> findByUserIdAndExchangedAtAfterOrderByExchangedAtDesc(
            Long userId, LocalDateTime startDate, Pageable pageable);
}