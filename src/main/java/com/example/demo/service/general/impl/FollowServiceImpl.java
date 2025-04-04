package com.example.demo.service.general.impl;

import com.example.demo.base.ApiResponse;
import com.example.demo.base.code.exception.CustomException;
import com.example.demo.base.status.ErrorStatus;
import com.example.demo.base.status.SuccessStatus;
import com.example.demo.domain.converter.FollowConverter;
import com.example.demo.domain.dto.Follow.FollowResponse;
import com.example.demo.entity.Follow;
import com.example.demo.entity.User;
import com.example.demo.repository.FollowRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.general.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class FollowServiceImpl implements FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    @Override
    public ApiResponse<List<FollowResponse>> getFollowedStores(Long userId) {
        User user = findUser(userId);

        List<FollowResponse> followedStores = followRepository.findByUser(user).stream()
                .map(follow -> FollowConverter.toResponse(follow, userRepository))
                .collect(Collectors.toList());

        return ApiResponse.of(SuccessStatus._OK, followedStores);
    }

    @Override
    public ApiResponse<Void> followStore(Long userId, Long storeId) {
        User user = findUser(userId);

        // 본인은 팔로우 할 수 없음
        if(userId.equals(storeId)) {
            throw new CustomException(ErrorStatus.FOLLOW_SELF); // 본인 팔로우 금지
        }

        // storeId가 존재하는 user 인지 확인
        boolean storeExists = userRepository.existsById(storeId);
        if (!storeExists) {
            throw new CustomException(ErrorStatus.FOLLOW_STORE_NOT_FOUND); // 존재하지 않는 스토어(유저)
        }

        // 이미 팔로우한 경우 예외 처리
        if (followRepository.findByUserAndStoreId(user, storeId).isPresent()) {
            throw new CustomException(ErrorStatus.FOLLOW_ALREADY);
        }

        // 팔로우 저장
        Follow follow = Follow.builder()
                .user(user)
                .storeId(storeId) // storeId를 직접 저장
                .build();
        followRepository.save(follow);

        return ApiResponse.of(SuccessStatus.FOLLOW_SUCCESS, null);
    }

    @Override
    public ApiResponse<Void> unfollowStore(Long userId, Long storeId) {
        User user = findUser(userId);

        // 팔로우 정보 조회
        Follow follow = followRepository.findByUserAndStoreId(user, storeId)
                .orElseThrow(() -> new CustomException(ErrorStatus.FOLLOW_NOT));

        // 팔로우 삭제
        followRepository.delete(follow);

        return ApiResponse.of(SuccessStatus.FOLLOW_UNFOLLOW_SUCCESS, null);
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));
    }
}