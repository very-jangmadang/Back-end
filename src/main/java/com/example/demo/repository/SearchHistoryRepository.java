package com.example.demo.repository;

import com.example.demo.entity.SearchHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Long> {
    Optional<SearchHistory> findByKeywordAndUserId(String keyword, Long userId);
    List<SearchHistory> findTop10ByUserIdOrderBySearchCountDesc(Long userId);
    List<SearchHistory> findTop10ByUserIdOrderBySearchedAtDesc(Long userId);
}
