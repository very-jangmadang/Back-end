package com.example.demo.service.general.impl;
import com.example.demo.base.code.exception.CustomException;
import com.example.demo.base.status.ErrorStatus;
import com.example.demo.base.status.SuccessStatus;
import com.example.demo.domain.converter.ReviewConverter;
import com.example.demo.domain.dto.Like.LikeResponseDTO;
import com.example.demo.domain.dto.Review.ReviewRequestDTO;
import com.example.demo.domain.dto.Review.ReviewResponseDTO;
import com.example.demo.domain.dto.Review.ReviewWithAverageDTO;
import com.example.demo.entity.Raffle;
import com.example.demo.entity.Review;
import com.example.demo.entity.User;
import com.example.demo.entity.base.enums.RaffleStatus;
import com.example.demo.repository.RaffleRepository;
import com.example.demo.repository.ReviewRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.general.ReviewService;
import com.example.demo.service.general.S3UploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
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
    @Transactional
    public ReviewResponseDTO addReview(ReviewRequestDTO.ReviewUploadDTO reviewRequest,Long userId) {

        // 0. 업로드 작성자 정보 가져오기
        Raffle raffle = raffleRepository.findByIdIncludeDeleted(reviewRequest.getRaffleId())
                .orElseThrow(() -> new CustomException(ErrorStatus.RAFFLE_NOT_FOUND));


        User reviewer = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));

        User user = raffle.getUser();

        // 당첨자만 리뷰를 남길 수 있음
        if (!raffle.getWinner().getId().equals(reviewer.getId())) {
            throw new CustomException(ErrorStatus.NOT_WINNER);  // 당첨자가 아닌 경우
        }

        // 중복 리뷰 확인
        Optional<Review> existingReview = reviewRepository.findByRaffleIdAndReviewerId(reviewRequest.getRaffleId(), reviewer.getId());
        if (existingReview.isPresent()) {
            throw new CustomException(ErrorStatus.DUPLICATE_REVIEW);  // 중복된 리뷰가 존재하는 경우
        }

        List<String> imageUrls = new ArrayList<>();

        if (reviewRequest.getImage() != null) {
            List<MultipartFile> images = Arrays.asList(reviewRequest.getImage());  // 이미지를 List로 받아옴
            imageUrls = s3UploadService.saveFile(images);  // 이미지 리스트를 saveFile에 전달하여 S3에 저장
        }

        Review review = ReviewConverter.toReview(reviewRequest,user, raffle,reviewer, imageUrls);

        reviewRepository.save(review);

        // 평균 평점 업데이트
        updateAverageScore(user, review.getScore(),true);

        return ReviewConverter.ToReviewResponseDTO(review);

    }

    // 리뷰 삭제
    @Transactional
    public void deleteReview(Long reviewId, Long userId){
        // 리뷰 내역 조회
        Review review = reviewRepository.findByIdIncludeDeletedRaffle(reviewId)
                .orElseThrow(() -> new CustomException(ErrorStatus.REVIEW_NOT_FOUND));

        // 요청자의 userId와 리뷰 작성자의 reviewerId 비교
        if (!review.getReviewer().getId().equals(userId))
            throw new CustomException(ErrorStatus.NO_DELETE_AUTHORITY);

        //삭제된 래플의 리뷰는 삭제 불가능함
        if (review.getRaffle() == null) {
            throw new CustomException(ErrorStatus.RAFFLE_NOT_FOUND); // 혹은 다른 에러 상태로
        }

        if (review.getRaffle().getRaffleStatus() == RaffleStatus.DELETED) {
            throw new CustomException(ErrorStatus.DELETED_RAFFLE);
        }

        // 삭제
        reviewRepository.deleteById(reviewId);

        updateAverageScore(review.getUser(), review.getScore(),false);

    }

    //리뷰 조회
    public ReviewWithAverageDTO getReviewsByUserId(Long userId) {

        // 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));

        // 사용자의 모든 후기 조회
        List<Review> reviews = reviewRepository.findAllByUser(user);

        List<ReviewResponseDTO> reviewResponseDTO = ReviewConverter.toReviewResponseDTOList(reviews);

        int reviewCount = reviews.size();

        double averageScore = user.getAverageScore();

        return new ReviewWithAverageDTO(reviewResponseDTO, averageScore, reviewCount);
    }


    private void updateAverageScore(User user, double score, boolean isAdd) {
        int currentReviewCount = user.getReviewCount();
        double currentAverageScore = user.getAverageScore();

        if (isAdd) {
            // 리뷰 추가 시
            double updatedAverageScore = ((currentAverageScore * currentReviewCount) + score) / (currentReviewCount + 1);
            user.setAverageScore(updatedAverageScore);
            user.setReviewCount(currentReviewCount + 1);
        } else {
            // 리뷰 삭제 시
            if (currentReviewCount > 1) {
                double updatedAverageScore = ((currentAverageScore * currentReviewCount) - score) / (currentReviewCount - 1);
                user.setAverageScore(updatedAverageScore);
                user.setReviewCount(currentReviewCount - 1);
            } else {
                // 리뷰가 더 이상 없을 경우 초기화
                user.setAverageScore(0.0);
                user.setReviewCount(0);
            }
        }

        // 사용자 저장
        userRepository.save(user);
    }

}

