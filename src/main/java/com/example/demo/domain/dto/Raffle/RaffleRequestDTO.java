package com.example.demo.domain.dto.Raffle;
import com.example.demo.entity.base.enums.Status;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

public class RaffleRequestDTO {

    @Getter
    @Setter
    public static class UploadDTO {

        @NotNull
        List<MultipartFile> files;
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
