package com.example.demo.domain.dto;
import com.example.demo.entity.base.enums.Status;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

public class RaffleRequestDTO {

    @Getter
    @Setter
    public static class UploadDTO {
        MultipartFile file;
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
