package com.example.demo.repository;

import com.example.demo.entity.Like;
import com.example.demo.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

}
