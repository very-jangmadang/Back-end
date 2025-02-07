package com.example.demo.domain.converter;

import com.example.demo.domain.dto.Follow.FollowResponse;
import com.example.demo.entity.Follow;
import lombok.experimental.UtilityClass;

@UtilityClass
public class FollowConverter {

    public FollowResponse toResponse(Follow follow) {
        return new FollowResponse(
                follow.getId(),
                follow.getUser().getId(),
                follow.getFollower().getId()
        );
    }
}