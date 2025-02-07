package com.example.demo.service.general;

import com.example.demo.base.ApiResponse;
import com.example.demo.domain.dto.Follow.FollowResponse;

import java.util.List;

public interface FollowService {
    ApiResponse<List<FollowResponse>> getFollowedStores(Long userId);
    ApiResponse<Void> followStore(Long userId, Long storeId);
    ApiResponse<Void> unfollowStore(Long userId, Long storeId);
}