package com.example.demo.domain.converter;

import com.example.demo.entity.Image;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class ImageConverter {

    public static List<Image> toImage(List<String> imageUrls) {

        AtomicInteger order = new AtomicInteger(1);

        List<Image> Images = imageUrls.stream()
                .map(url -> Image.builder()
                        .ImageUrl(url)
                        .order(order.getAndIncrement())
                        .build())
                .collect(Collectors.toList());
        return Images;
    }
}
