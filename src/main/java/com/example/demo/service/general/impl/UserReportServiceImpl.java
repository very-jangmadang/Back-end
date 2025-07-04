package com.example.demo.service.general.impl;

import com.example.demo.base.code.exception.CustomException;
import com.example.demo.base.status.ErrorStatus;
import com.example.demo.domain.dto.Report.RaffleReportRequestDTO;
import com.example.demo.domain.dto.Report.RaffleReportResponseDTO;
import com.example.demo.domain.dto.Report.UserReportRequestDTO;
import com.example.demo.domain.dto.Report.UserReportResponseDTO;
import com.example.demo.entity.Raffle;
import com.example.demo.entity.Report;
import com.example.demo.entity.User;
import com.example.demo.repository.RaffleRepository;
import com.example.demo.repository.ReportRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.general.S3UploadService;
import com.example.demo.service.general.UserReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.example.demo.entity.base.enums.ReportStatus.PENDING;

@Service
@RequiredArgsConstructor
@Transactional
public class UserReportServiceImpl implements UserReportService {

    private final UserRepository userRepository;
    private final ReportRepository reportRepository;
    private final S3UploadService s3UploadService;
    private final RaffleRepository raffleRepository;

    @Override
    public UserReportResponseDTO reportUser(Long userId, UserReportRequestDTO userReportRequestDTO, List<MultipartFile> images){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long currentUserId = Long.parseLong(authentication.getName());
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(()->new CustomException(ErrorStatus.USER_NOT_FOUND));

        User reportedUser = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));

        List<String> imageUrls = (images != null && !images.isEmpty())
                ? s3UploadService.saveFile(images)
                : List.of();

        Report report = Report.builder()
                .reporter(currentUser)
                .reportedUser(reportedUser)
                .reasonType(userReportRequestDTO.getReasonType())
                .reasonDetail(userReportRequestDTO.getReasonDetail())
                .reportStatus(PENDING)
                .imageUrls(imageUrls)
                .build();

        reportRepository.save(report);

        return UserReportResponseDTO.builder()
                .userId(userId)
                .build();
    }

    @Override
    public RaffleReportResponseDTO reportRaffle(Long raffleId, RaffleReportRequestDTO raffleReportRequestDTO, List<MultipartFile> images) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long currentUserId = Long.parseLong(authentication.getName());
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));

        Raffle raffle = raffleRepository.findById(raffleId)
                .orElseThrow(() -> new CustomException(ErrorStatus.RAFFLE_NOT_FOUND));

        User reportedUser = raffle.getUser();

        List<String> imageUrls = (images != null && !images.isEmpty())
                ? s3UploadService.saveFile(images)
                : List.of();

        Report report = Report.builder()
                .reporter(currentUser)
                .reportedUser(reportedUser)
                .reasonType(raffleReportRequestDTO.getReasonType())
                .reasonDetail(raffleReportRequestDTO.getReasonDetail())
                .reportStatus(PENDING)
                .imageUrls(imageUrls)
                .build();

        reportRepository.save(report);

        return RaffleReportResponseDTO.builder()
                .userId(reportedUser.getId())
                .build();
    }
}
