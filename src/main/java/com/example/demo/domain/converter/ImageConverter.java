package com.example.demo.domain.converter;

import com.example.demo.entity.Image;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class ImageConverter {

    public static List<Image> toImage(List<String> imageUrls) {

        AtomicInteger order = new AtomicInteger(1); // 사진 여러장일 경우 1부터 시작

        List<Image> Images = imageUrls.stream()
                .map(url -> Image.builder()
                        .ImageUrl(url)
                        .order(order.getAndIncrement())
                        .build())
                .collect(Collectors.toList());
        return Images;
    }

}
