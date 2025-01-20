package com.example.demo.service.general.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.example.demo.base.code.exception.CustomException;
import com.example.demo.base.status.ErrorStatus;
import com.example.demo.service.general.S3UploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class S3UploadServiceImpl implements S3UploadService {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public List<String> saveFile(List<MultipartFile> multipartFiles) {

        List<String> imageUrls = new ArrayList<>();

        for (MultipartFile multipartFile : multipartFiles) {
            String originalFilename = multipartFile.getOriginalFilename();
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(multipartFile.getSize());
            metadata.setContentType(multipartFile.getContentType());

            try {
                amazonS3.putObject(bucket, originalFilename, multipartFile.getInputStream(), metadata);
            } catch (IOException e) {
                throw new CustomException(ErrorStatus.IMAGE_UPLOAD_FAILED);
            }

            imageUrls.add(amazonS3.getUrl(bucket, originalFilename).toString());

        }
        return imageUrls;
    }
}