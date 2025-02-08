package com.example.demo.domain.converter;

import com.example.demo.domain.dto.Follow.FollowResponse;
import com.example.demo.entity.Follow;
import lombok.experimental.UtilityClass;

@UtilityClass
public class FollowConverter {

    public FollowResponse toResponse(Follow follow) {
        return new FollowResponse(
                follow.getStoreId(),
                follow.getFollower().getProfileImageUrl()  // 유저의 프로필 이미지
        );
    }
}