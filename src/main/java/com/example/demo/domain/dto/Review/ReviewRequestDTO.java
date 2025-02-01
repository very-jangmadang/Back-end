package com.example.demo.domain.dto.Review;

import io.micrometer.common.lang.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.swing.plaf.multi.MultiOptionPaneUI;
import java.time.LocalDateTime;
import java.util.List;

public class ReviewRequestDTO{


    @Getter
    @Setter
    public static class ReviewUploadDTO {
        @NotNull(message = "raffle ID must not be null")
        private Long raffleId;
        private Float score;
        private String text;

        private MultipartFile[] image = new MultipartFile[0];

    }


    public static class ReviewResponseDTO {
        private Long reviewId;
        private Long userId;
        private Long reviewerId;
        private int score;
        private String text;
        private String imageUrl;
        private LocalDateTime timestamp;
    }
}
