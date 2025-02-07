package com.example.demo.controller.general;

import com.example.demo.base.ApiResponse;
import com.example.demo.controller.BaseController;
import com.example.demo.domain.dto.Follow.FollowResponse;
import com.example.demo.service.general.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/member/follow")
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;
    private final BaseController baseController;

    // 특정 유저가 팔로우한 상점 목록 조회
    @GetMapping("/list")
    public ApiResponse<List<FollowResponse>> getFollowedStores() {
        Long userId = baseController.getCurrentUserId();
        return followService.getFollowedStores(userId);
    }

    // 팔로우하기
    @PostMapping("/")
    public ApiResponse<Void> followStore(@RequestParam Long storeId) {
        Long userId = baseController.getCurrentUserId();
        return followService.followStore(userId, storeId);
    }

    // 팔로우 취소
    @DeleteMapping("/cancel")
    public ApiResponse<Void> unfollowStore(@RequestParam Long storeId) {
        Long userId = baseController.getCurrentUserId();
        return followService.unfollowStore(userId, storeId);
    }
}