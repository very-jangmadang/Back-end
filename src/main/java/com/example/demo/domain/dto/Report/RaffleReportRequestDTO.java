package com.example.demo.domain.dto.Report;

import com.example.demo.entity.base.enums.ReasonType;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RaffleReportRequestDTO {
    ReasonType reasonType;  // 카테고리
    String reasonDetail;    // 내용
}
