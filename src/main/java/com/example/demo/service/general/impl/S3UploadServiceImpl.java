package com.example.demo.service.general.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.example.demo.base.code.exception.CustomException;
import com.example.demo.base.status.ErrorStatus;
import com.example.demo.service.general.S3UploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class S3UploadServiceImpl implements S3UploadService {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public List<String> saveFile(List<MultipartFile> multipartFiles) {

        List<String> imageUrls = new ArrayList<>();

        for (MultipartFile multipartFile : multipartFiles) {
            String uniqueFileName = generateUniqueFileName(multipartFile.getOriginalFilename());
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(multipartFile.getSize());
            metadata.setContentType(multipartFile.getContentType());

            try {
                amazonS3.putObject(bucket, uniqueFileName, multipartFile.getInputStream(), metadata);
            } catch (IOException e) {
                throw new CustomException(ErrorStatus.IMAGE_UPLOAD_FAILED);
            }

            imageUrls.add(amazonS3.getUrl(bucket, uniqueFileName).toString());

        }
        return imageUrls;
    }


    // 단일 파일 업로드 처리 (프로필 사진을 위함)
    public String saveSingleFile(MultipartFile multipartFile) {

        String uniqueFileName = generateUniqueFileName(multipartFile.getOriginalFilename());
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(multipartFile.getSize());
        metadata.setContentType(multipartFile.getContentType());

        try {
            // S3에 파일 업로드
            amazonS3.putObject(bucket, uniqueFileName, multipartFile.getInputStream(), metadata);
        } catch (IOException e) {
            throw new CustomException(ErrorStatus.IMAGE_UPLOAD_FAILED);
        }

        // 업로드된 파일의 URL 반환
        return amazonS3.getUrl(bucket, uniqueFileName).toString();
    }

    private String generateUniqueFileName(String originalFilename) {
        String extension = "";
        int lastDotIndex = originalFilename.lastIndexOf(".");
        if (lastDotIndex != -1) {
            extension = originalFilename.substring(lastDotIndex);
        }
        return UUID.randomUUID().toString() + extension;
    }
}