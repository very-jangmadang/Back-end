package com.example.demo.service.general;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface S3UploadService {

    // 이미지 S3에 업로드
    public List<String> saveFile(List<MultipartFile> multipartFile);
}