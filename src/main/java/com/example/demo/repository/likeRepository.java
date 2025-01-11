package com.example.demo.repository;

import com.example.demo.entity.Like;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.FluentQuery;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public interface likeRepository extends JpaRepository<Like, Long> {

     void deleteByUserIdAndRappleId(Long userId, Long rappleId);

    // 특정 사용자가 찜한 rappleId 목록 조회
    List<Like> findByUserId(Long userId);
}
