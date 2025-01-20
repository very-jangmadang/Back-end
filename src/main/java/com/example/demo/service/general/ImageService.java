package com.example.demo.service.general;

import com.example.demo.entity.Image;

import java.util.List;

public interface ImageService {
    List<Image> saveImages(List<String> imageUrls);
}
