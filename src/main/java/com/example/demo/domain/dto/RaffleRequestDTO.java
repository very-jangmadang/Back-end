package com.example.demo.domain.dto;
import com.example.demo.entity.base.enums.ItemStatus;
import lombok.Getter;

import java.time.LocalDateTime;

public class RaffleRequestDTO {

    @Getter
    public static class UploadDTO {
        String category;
        String name;
        ItemStatus status;
        String description;
        int ticketNum;
        int minTicket;
        LocalDateTime startAt;
        LocalDateTime endAt;
    }
}
