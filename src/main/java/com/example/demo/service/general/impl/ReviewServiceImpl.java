package com.example.demo.service.general.impl;
import com.example.demo.base.ApiResponse;
import com.example.demo.base.code.exception.CustomException;
import com.example.demo.base.status.ErrorStatus;
import com.example.demo.base.status.SuccessStatus;
import com.example.demo.domain.converter.RaffleConverter;
import com.example.demo.domain.converter.ReviewConverter;
import com.example.demo.domain.dto.Like.LikeResponseDTO;
import com.example.demo.domain.dto.Review.ReviewDeleteDTO;
import com.example.demo.domain.dto.Review.ReviewRequestDTO;
import com.example.demo.domain.dto.Review.ReviewResponseDTO;
import com.example.demo.entity.Category;
import com.example.demo.entity.Raffle;
import com.example.demo.entity.Review;
import com.example.demo.entity.User;
import com.example.demo.repository.ReviewRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.general.ReviewService;
import com.example.demo.service.general.S3UploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final S3UploadService s3UploadService;

    // 리뷰 작성
    public ReviewResponseDTO addReview(ReviewRequestDTO.ReviewUploadDTO reviewRequest) {

        // 0. 업로드 작성자 정보 가져오기 (JWT 기반 인증 후 추후 구현 예정)
        User user = userRepository.findById(reviewRequest.getUserId())
                .orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));

        User reviewer = userRepository.findById(reviewRequest.getReviewerId())
                .orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));


        List<String> imageUrls = new ArrayList<>();

        if (reviewRequest.getImage() != null) {
            List<MultipartFile> images = Arrays.asList(reviewRequest.getImage());  // 이미지를 List로 받아옴
            imageUrls = s3UploadService.saveFile(images);  // 이미지 리스트를 saveFile에 전달하여 S3에 저장
        }

        Review review = ReviewConverter.toReview(reviewRequest, user ,reviewer,imageUrls);

        reviewRepository.save(review);

        return ReviewConverter.ToReviewResponseDTO(review);

    }

    // 리뷰 삭제
    @Transactional
    public void deleteReview(Long reviewId, ReviewDeleteDTO reviewDelete) {

        // 리뷰 내역 조회
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("해당 리뷰가 없습니다"));

        // 삭제
        reviewRepository.deleteById(reviewId);

    }
}

