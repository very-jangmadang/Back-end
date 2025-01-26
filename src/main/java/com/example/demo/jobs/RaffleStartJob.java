package com.example.demo.jobs;

import com.example.demo.base.code.exception.CustomException;
import com.example.demo.base.status.ErrorStatus;
import com.example.demo.entity.Raffle;
import com.example.demo.entity.base.enums.RaffleStatus;
import com.example.demo.repository.RaffleRepository;
import lombok.RequiredArgsConstructor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class RaffleStartJob implements Job {

    private final RaffleRepository raffleRepository;

    @Override
    @Transactional
    public void execute(JobExecutionContext context) {

        Long raffleId = context.getJobDetail().getJobDataMap().getLong("raffleId");

        Raffle raffle = raffleRepository.findById(raffleId)
                .orElseThrow(() -> new CustomException(ErrorStatus.RAFFLE_NOT_FOUND));

        raffle.setRaffleStatus(RaffleStatus.ACTIVE);
        raffleRepository.save(raffle);

    }
}
