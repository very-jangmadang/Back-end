package com.example.demo.domain.dto;
import com.example.demo.entity.base.enums.Status;
import lombok.Getter;

import java.time.LocalDateTime;

public class RaffleRequestDTO {

    @Getter
    public static class UploadDTO {
        String category;
        String name;
        Status status;
        String description;
        int ticketNum;
        int minTicket;
        LocalDateTime startAt;
        LocalDateTime endAt;
    }
}
