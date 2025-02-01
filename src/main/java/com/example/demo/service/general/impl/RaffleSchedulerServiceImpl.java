package com.example.demo.service.general.impl;

import com.example.demo.base.code.exception.CustomException;
import com.example.demo.base.status.ErrorStatus;
import com.example.demo.entity.Raffle;
import com.example.demo.jobs.*;
import com.example.demo.service.general.RaffleSchedulerService;
import lombok.RequiredArgsConstructor;
import org.quartz.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class RaffleSchedulerServiceImpl implements RaffleSchedulerService {

    private final Scheduler scheduler;

    @Override
    public void scheduleRaffleJob(Raffle raffle, boolean isStart) {
        try {

            if (!scheduler.isStarted()) {
                scheduler.start();
            }

            JobDetail jobDetail = isStart ? buildStartJobDetail(raffle) : buildEndJobDetail(raffle);
            Trigger trigger = buildJobTrigger(isStart ? raffle.getStartAt() : raffle.getEndAt());

            scheduler.scheduleJob(jobDetail, trigger);

        } catch (SchedulerException e) {
            Throwable cause = e.getCause();  // 원인 예외 분석

            // 예외 처리 로직
            if (cause instanceof JobPersistenceException) {
                // JobPersistenceException: 작업을 저장할 수 없을 때 발생
                throw new CustomException(ErrorStatus.JOB_STORE_FAILED);
            } else if (cause instanceof JobExecutionException) {
                // JobExecutionException: 작업 실행에 문제가 있을 때 발생
                throw new CustomException(ErrorStatus.JOB_EXECUTION_FAILED);
//            } else if (cause instanceof JobInterruptException) {
//              // JobInterruptException: 작업이 인터럽트되었을 때 발생
//                throw new CustomException(ErrorStatus.JOB_INTERRUPT);
            } else {
                // 그 외 다른 오류들
                throw new CustomException(ErrorStatus.JOB_UNKNOWN);
            }
        }
    }

    private JobDetail buildStartJobDetail(Raffle raffle) {
        return JobBuilder.newJob(RaffleStartJob.class)
                .withIdentity("Raffle_" + raffle.getId() + "_START")
                .usingJobData("raffleId", raffle.getId())
                .storeDurably()
                .build();
    }

    private JobDetail buildEndJobDetail(Raffle raffle) {
        return JobBuilder.newJob(RaffleEndJob.class)
                .withIdentity("Raffle_" + raffle.getId() + "_END")
                .usingJobData("raffleId", raffle.getId())
                .storeDurably()
                .build();
    }

    private Trigger buildJobTrigger(LocalDateTime time) {
        LocalDateTime adjustedTime = time.withSecond(0).withNano(0);
        Date startDate = Date.from(adjustedTime.atZone(java.time.ZoneId.systemDefault()).toInstant());

        return TriggerBuilder.newTrigger()
                .startAt(startDate)
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withMisfireHandlingInstructionFireNow())
                .build();
    }
}

