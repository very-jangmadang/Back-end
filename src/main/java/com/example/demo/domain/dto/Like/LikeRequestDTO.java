package com.example.demo.domain.dto.Like;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class LikeRequestDTO{
        @NotBlank
        private Long userId;
}
