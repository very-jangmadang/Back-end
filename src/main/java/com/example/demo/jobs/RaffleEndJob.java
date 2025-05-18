package com.example.demo.jobs;

import com.example.demo.base.code.exception.CustomException;
import com.example.demo.base.status.ErrorStatus;
import com.example.demo.entity.Apply;
import com.example.demo.entity.Raffle;
import com.example.demo.entity.base.enums.RaffleStatus;
import com.example.demo.repository.ApplyRepository;
import com.example.demo.repository.RaffleRepository;
import com.example.demo.service.general.DrawService;
import com.example.demo.service.general.EmailService;
import com.example.demo.service.general.NotificationService;
import com.example.demo.service.general.SchedulerService;
import lombok.RequiredArgsConstructor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RaffleEndJob implements Job {

    private final RaffleRepository raffleRepository;
    private final EmailService emailService;
    private final DrawService drawService;
    private final SchedulerService schedulerService;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public void execute(JobExecutionContext context) {
        Long raffleId = context.getJobDetail().getJobDataMap().getLong("raffleId");

        Raffle raffle = raffleRepository.findById(raffleId)
                .orElseThrow(() -> new CustomException(ErrorStatus.RAFFLE_NOT_FOUND));

        List<Apply> applyList = raffle.getApplyList();
        int applyCount = applyList.size();

        if (applyCount == 0) {
            raffle.setRaffleStatus(RaffleStatus.CANCELLED);
            emailService.sendOwnerCancelEmail(raffle);
            return;
        }

        if (applyCount * raffle.getTicketNum() < raffle.getMinTicket()) {
            raffle.setRaffleStatus(RaffleStatus.UNFULFILLED);

            schedulerService.scheduleDrawJob(raffle);
            emailService.sendOwnerUnfulfilledEmail(raffle);
            return;
        }

        raffle.setRaffleStatus(RaffleStatus.ENDED);

        drawService.draw(raffle, applyList);

        notificationService.sendHostForEndedRaffle(raffle);
        notificationService.sendApplicantForEnd((raffle));
    }
}