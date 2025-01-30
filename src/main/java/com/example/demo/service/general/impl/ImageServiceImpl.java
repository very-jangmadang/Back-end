package com.example.demo.service.general.impl;

import com.example.demo.domain.converter.ImageConverter;
import com.example.demo.entity.Image;
import com.example.demo.repository.ImageRepository;
import com.example.demo.service.general.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

    public List<Image> saveImages(List<String> imageUrls) {
        return ImageConverter.toImage(imageUrls); // 이미지 엔티티로 변환
    }
}
