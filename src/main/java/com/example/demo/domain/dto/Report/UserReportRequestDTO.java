package com.example.demo.domain.dto.Report;
import com.example.demo.entity.base.enums.ReasonType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserReportRequestDTO {
    ReasonType reasonType;  // 카테고리
    String reasonDetail;    // 내용
}