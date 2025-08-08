package com.example.demo.domain.dto.Raffle;
import com.example.demo.entity.base.enums.ItemStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

public class RaffleRequestDTO {

    @Getter
    @Builder
    public static class UploadDTO {

//        int minTicket;
//        int deliveryFee;

        @NotNull
        List<MultipartFile> files;
        String category;
        String name;
//        ItemStatus itemStatus;
        String description;
        int ticketNum;
        LocalDateTime startAt;
        LocalDateTime endAt;
    }
}
