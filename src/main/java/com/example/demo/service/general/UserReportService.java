package com.example.demo.service.general;

import com.example.demo.domain.dto.Report.RaffleReportRequestDTO;
import com.example.demo.domain.dto.Report.RaffleReportResponseDTO;
import com.example.demo.domain.dto.Report.UserReportRequestDTO;
import com.example.demo.domain.dto.Report.UserReportResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserReportService {

    UserReportResponseDTO reportUser(Long userId, UserReportRequestDTO userReportRequestDTO, List<MultipartFile> images);

    RaffleReportResponseDTO reportRaffle(Long raffleId, RaffleReportRequestDTO raffleReportRequestDTO, List<MultipartFile> images);
}
