package com.example.demo.domain.dto.Review;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@AllArgsConstructor
public class ReviewImageDTO {
    private List<MultipartFile> images;
    private String imageUrl;
}
