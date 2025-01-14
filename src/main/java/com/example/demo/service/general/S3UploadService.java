package com.example.demo.service.general;

import org.springframework.web.multipart.MultipartFile;

public interface S3UploadService {

    // 이미지 업로드 기능
    public String saveFile(MultipartFile multipartFile);

    // 이미지 url 가져오기 기능(필요한가?)
}
