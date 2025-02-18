package com.example.demo.domain.converter;

import com.example.demo.domain.dto.Follow.FollowResponse;
import com.example.demo.entity.Follow;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import lombok.experimental.UtilityClass;

@UtilityClass
public class FollowConverter {

    public FollowResponse toResponse(Follow follow, UserRepository userRepository) {
        // storeId에 해당하는 유저 찾기
        User store = userRepository.findById(follow.getStoreId()).orElse(null);

        return new FollowResponse(
                follow.getStoreId(),
                (store != null) ? store.getNickname() : null, // store의 닉네임 반환
                (store != null) ? store.getProfileImageUrl() : null // store의 프로필 이미지 반환
        );
    }
}