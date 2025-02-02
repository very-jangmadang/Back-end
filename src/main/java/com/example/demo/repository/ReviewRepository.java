package com.example.demo.repository;

import com.example.demo.entity.Inquiry;
import com.example.demo.entity.Like;
import com.example.demo.entity.Review;
import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequestEntityConverter;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findAllByUser(User user);

    Optional<Review> findByRaffleIdAndReviewerId(Long raffleId, Long reviewerId);
}
