package com.example.demo.service.general.impl;
import com.example.demo.base.ApiResponse;
import com.example.demo.base.code.exception.CustomException;
import com.example.demo.base.status.ErrorStatus;
import com.example.demo.base.status.SuccessStatus;
import com.example.demo.domain.converter.ReviewConverter;
import com.example.demo.domain.dto.Like.LikeResponseDTO;
import com.example.demo.domain.dto.Review.ReviewDeleteDTO;
import com.example.demo.domain.dto.Review.ReviewRequestDTO;
import com.example.demo.domain.dto.Review.ReviewResponseDTO;
import com.example.demo.entity.Raffle;
import com.example.demo.entity.Review;
import com.example.demo.entity.User;
import com.example.demo.repository.RaffleRepository;
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
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final RaffleRepository raffleRepository;
    private final S3UploadService s3UploadService;

    // 리뷰 작성
    public ReviewResponseDTO addReview(ReviewRequestDTO.ReviewUploadDTO reviewRequest) {

        // 0. 업로드 작성자 정보 가져오기 (JWT 기반 인증 후 추후 구현 예정)
        Raffle raffle = raffleRepository.findById(reviewRequest.getRaffleId())
                .orElseThrow(() -> new CustomException(ErrorStatus.RAFFLE_NOT_FOUND));

        User user = userRepository.findById(reviewRequest.getUserId())
                .orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));

        User reviewer = userRepository.findById(reviewRequest.getReviewerId())
                .orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));

        //래플의 주최자
        if (!raffle.getUser().getId().equals(reviewRequest.getUserId())) {
            throw new CustomException(ErrorStatus.RAFFLE_USER_MISMATCH); // 주최자와 사용자가 일치하지 않음
        }

        // 당첨자만 리뷰를 남길 수 있음
        if (!raffle.getWinner().getId().equals(reviewRequest.getReviewerId())) {
            throw new CustomException(ErrorStatus.NOT_WINNER);  // 당첨자가 아닌 경우
        }

        // 중복 리뷰 확인
        Optional<Review> existingReview = reviewRepository.findByRaffleIdAndReviewerId(reviewRequest.getRaffleId(), reviewRequest.getReviewerId());
        if (existingReview.isPresent()) {
            throw new CustomException(ErrorStatus.DUPLICATE_REVIEW);  // 중복된 리뷰가 존재하는 경우
        }


        List<String> imageUrls = new ArrayList<>();

        if (reviewRequest.getImage() != null) {
            List<MultipartFile> images = Arrays.asList(reviewRequest.getImage());  // 이미지를 List로 받아옴
            imageUrls = s3UploadService.saveFile(images);  // 이미지 리스트를 saveFile에 전달하여 S3에 저장
        }

        Review review = ReviewConverter.toReview(reviewRequest,raffle,user,reviewer,imageUrls);

        reviewRepository.save(review);

        return ReviewConverter.ToReviewResponseDTO(review);

    }

    // 리뷰 삭제
    @Transactional
    public void deleteReview(Long reviewId, ReviewDeleteDTO reviewDelete) {

        // 리뷰 내역 조회
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new CustomException(ErrorStatus.REVIEW_NOT_FOUND));

        // 삭제
        reviewRepository.deleteById(reviewId);

    }

    //리뷰 조회
    public List<ReviewResponseDTO> getReviewsByUserId(Long userId) {

        // 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));

        // 사용자의 모든 후기 조회
        List<Review> reviews = reviewRepository.findAllByUser(user);

        return reviews.stream()
                .map(review -> new ReviewResponseDTO(
                        review.getId(),               // reviewId
                        review.getUser().getId(),     // userId
                        review.getRaffle().getId(), // raffleId
                        review.getReviewer().getId(), //reviewerId
                        review.getScore(),            // score
                        review.getText(),             // text
                        review.getImageUrls(),        // imageUrls
                        review.getCreatedAt()          // timestamp
                ))
                .collect(Collectors.toList());
    }
}

