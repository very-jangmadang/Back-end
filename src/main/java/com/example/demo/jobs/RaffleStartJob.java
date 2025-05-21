package com.example.demo.jobs;

import com.example.demo.base.code.exception.CustomException;
import com.example.demo.base.status.ErrorStatus;
import com.example.demo.entity.Like;
import com.example.demo.entity.Raffle;
import com.example.demo.entity.User;
import com.example.demo.entity.base.enums.RaffleStatus;
import com.example.demo.repository.LikeRepository;
import com.example.demo.repository.RaffleRepository;
import com.example.demo.service.general.EmailService;
import com.example.demo.service.general.NotificationService;
import lombok.RequiredArgsConstructor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RaffleStartJob implements Job {

    private final RaffleRepository raffleRepository;
    private final LikeRepository likeRepository;
    private final EmailService emailService;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public void execute(JobExecutionContext context) {

        Long raffleId = context.getJobDetail().getJobDataMap().getLong("raffleId");

        Raffle raffle = raffleRepository.findById(raffleId)
                .orElseThrow(() -> new CustomException(ErrorStatus.RAFFLE_NOT_FOUND));

        raffle.setRaffleStatus(RaffleStatus.ACTIVE);

        List<Like> likeList = likeRepository.findByRaffleWithUser(raffle);
        List<User> users = likeList.stream()
                .map(Like::getUser)
                .collect(Collectors.toList());

        int batchSize = 100;
        int totalSize = users.size();
        for (int i = 0; i < totalSize; i += batchSize) {
            int end = Math.min(i + batchSize, totalSize);
            List<User> batchUsers = users.subList(i, end);

            emailService.sendBatchRaffleOpenEmail(batchUsers, raffle);
        }

        emailService.sendOwnerRaffleOpenEmail(raffle);
        notificationService.sendAllForOpen(raffle);

    }
}
