package com.example.demo.domain.dto.Intent;

import lombok.Getter;

public class OrderIntentRequestDTO {

    @Getter
    public static class CreateIntentRequestDTO {
        private Integer quantity;
    }
}
