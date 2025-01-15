package com.example.demo.repository;

import com.example.demo.entity.Review;
import com.example.demo.entity.ReviewImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewImageRepository extends JpaRepository<ReviewImage, Long> {
}
