package com.example.demo.repository;

import com.example.demo.entity.Payment.TopUp;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TopUpRepository extends JpaRepository<TopUp, Long> {
    Optional<TopUp> findByTxId(String txId);
}