package com.example.demo.repository;

import com.example.demo.entity.SearchHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Long> {
    Optional<SearchHistory> findByKeywordAndUserId(String keyword, Long userId);
    @Query("SELECT sh FROM SearchHistory sh " +
            "GROUP BY sh.keyword " +
            "ORDER BY MAX(sh.searchCount) DESC, MAX(sh.searchedAt) DESC LIMIT 10")
    List<SearchHistory> findTop10UniqueOrderBySearchCountDesc();
    List<SearchHistory> findTop10ByUserIdOrderBySearchedAtDesc(Long userId);
}
