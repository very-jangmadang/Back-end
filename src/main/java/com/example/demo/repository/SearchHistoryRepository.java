package com.example.demo.repository;

import com.example.demo.entity.SearchHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Long> {
    Optional<SearchHistory> findByKeywordAndUserId(String keyword, Long userId);
}
