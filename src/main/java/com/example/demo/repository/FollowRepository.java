package com.example.demo.repository;

import com.example.demo.entity.Follow;
import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> {
    List<Follow> findByUser(User user);
    Optional<Follow> findByUserAndStoreId(User user, Long storeId);

    boolean existsByUserIdAndStoreId(Long userId, Long storeId);
}