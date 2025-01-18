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

    ImageRepository imageRepository;

    public List<Image> saveImages(List<String> imageUrls) {

        //1. 전달받은 imageUrls를 통해 Image 엔티티로 변환
        return ImageConverter.toImage(imageUrls);

//        //2. 변환 후 영속성 컨텍스트에 저장
//        return imageRepository.saveAll(images);
    }
}
