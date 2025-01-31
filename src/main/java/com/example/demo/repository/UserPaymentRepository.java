package com.example.demo.repository;

import com.example.demo.entity.UserPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserPaymentRepository extends JpaRepository<UserPayment, Long> {
    Optional<UserPayment> findByUserId(String userId);
}