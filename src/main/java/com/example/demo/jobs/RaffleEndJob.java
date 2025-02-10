package com.example.demo.jobs;

import com.example.demo.base.code.exception.CustomException;
import com.example.demo.base.status.ErrorStatus;
import com.example.demo.entity.Apply;
import com.example.demo.entity.Raffle;
import com.example.demo.entity.base.enums.RaffleStatus;
import com.example.demo.repository.ApplyRepository;
import com.example.demo.repository.RaffleRepository;
import com.example.demo.service.general.DrawSchedulerService;
import com.example.demo.service.general.DrawService;
import com.example.demo.service.general.EmailService;
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
    private final ApplyRepository applyRepository;
    private final EmailService emailService;
    private final DrawService drawService;
    private final DrawSchedulerService drawSchedulerService;

    @Override
    @Transactional
    public void execute(JobExecutionContext context) {
        Long raffleId = context.getJobDetail().getJobDataMap().getLong("raffleId");

        Raffle raffle = raffleRepository.findById(raffleId)
                .orElseThrow(() -> new CustomException(ErrorStatus.RAFFLE_NOT_FOUND));

        int applyCount = applyRepository.countByRaffle(raffle);
        List<Apply> applyList = applyRepository.findByRaffle(raffle);

        if (applyCount * raffle.getTicketNum() < raffle.getMinTicket()) {
            updateRaffleStatus(raffle, RaffleStatus.UNFULFILLED);

            drawSchedulerService.scheduleDrawJob(raffle);
            emailService.sendOwnerUnfulfilledEmail(raffle);
            return;
        }

        updateRaffleStatus(raffle, RaffleStatus.ENDED);

        if (applyList == null || applyList.isEmpty()) {
            updateRaffleStatus(raffle, RaffleStatus.CANCELLED);
            return;
        }

        drawService.draw(raffle, applyList);
    }

    private void updateRaffleStatus(Raffle raffle, RaffleStatus status) {
        raffle.setRaffleStatus(status);
        raffleRepository.save(raffle);
    }
}