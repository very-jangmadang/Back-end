package com.example.demo.repository;

import com.example.demo.entity.Payment.OrderIntent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// OrderIntentRepository.java
public interface OrderIntentRepository extends JpaRepository<OrderIntent, Long> {
    Optional<OrderIntent> findByJti(String jti);
}
