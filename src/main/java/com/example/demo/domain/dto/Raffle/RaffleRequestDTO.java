package com.example.demo.domain.dto.Raffle;
import com.example.demo.entity.base.enums.ItemStatus;
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
        ItemStatus itemStatus;
        String description;
        int ticketNum;
        int minTicket;
        LocalDateTime startAt;
        LocalDateTime endAt;
    }
}
