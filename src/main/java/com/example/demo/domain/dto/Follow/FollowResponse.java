package com.example.demo.domain.dto.Follow;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FollowResponse {
    private Long storeId;
    private String storeName;
    private String profileImg;
}