package com.example.demo.jobs;

import com.example.demo.base.code.exception.CustomException;
import com.example.demo.base.status.ErrorStatus;
import com.example.demo.entity.Apply;
import com.example.demo.entity.Like;
import com.example.demo.entity.Raffle;
import com.example.demo.entity.User;
import com.example.demo.entity.base.enums.RaffleStatus;
import com.example.demo.repository.LikeRepository;
import com.example.demo.repository.RaffleRepository;
import com.example.demo.service.general.EmailService;
import lombok.RequiredArgsConstructor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RaffleStartJob implements Job {

    private final RaffleRepository raffleRepository;
    private final LikeRepository likeRepository;
    private final EmailService emailService;

    @Override
    @Transactional
    public void execute(JobExecutionContext context) {

        Long raffleId = context.getJobDetail().getJobDataMap().getLong("raffleId");

        Raffle raffle = raffleRepository.findById(raffleId)
                .orElseThrow(() -> new CustomException(ErrorStatus.RAFFLE_NOT_FOUND));

        raffle.setRaffleStatus(RaffleStatus.ACTIVE);
        raffleRepository.save(raffle);

        List<Like> likeList = likeRepository.findByRaffle(raffle);
        for (Like like : likeList)
            emailService.sendRaffleOpenEmail(raffle, like.getUser());

        emailService.sendOwnerRaffleOpenEmail(raffle);

    }
}
