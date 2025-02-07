package com.example.demo.domain.dto.Follow;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FollowResponse {
    private Long followId;
    private Long userId;
    private Long storeId;
}