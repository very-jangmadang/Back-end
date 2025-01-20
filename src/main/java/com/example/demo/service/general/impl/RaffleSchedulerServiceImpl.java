package com.example.demo.service.general.impl;

import com.example.demo.base.code.exception.CustomException;
import com.example.demo.base.status.ErrorStatus;
import com.example.demo.entity.Raffle;
import com.example.demo.jobs.*;
import lombok.RequiredArgsConstructor;
import org.quartz.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class RaffleSchedulerServiceImpl {

    private final Scheduler scheduler;

    public void scheduleRaffleStart(Raffle raffle) {
        try {

            JobDetail jobDetail = buildStartJobDetail(raffle);
            Trigger trigger = buildJobTrigger(raffle.getStartAt());

            scheduler.scheduleJob(jobDetail, trigger);

        } catch (SchedulerException e) {
            Throwable cause = e.getCause();  // 원인 예외 분석

            if (cause instanceof JobPersistenceException) {
                // JobPersistenceException: 작업을 저장할 수 없을 때 발생
                throw new CustomException(ErrorStatus.JOB_STORE_FAILED);
            } else if (cause instanceof JobExecutionException) {
                // JobExecutionException: 작업 실행에 문제가 있을 때 발생
                throw new CustomException(ErrorStatus.JOB_EXECUTION_FAILED);

//            } else if (cause instanceof JobInterruptException) {
//                // JobInterruptException: 작업이 인터럽트되었을 때 발생
//                throw new CustomException(ErrorStatus.JOB_INTERRUPT);

            } else {
                // 그 외 다른 오류들
                throw new CustomException(ErrorStatus.JOB_UNKNOWN);
            }
        }
    }

    public void scheduleRaffleEnd(Raffle raffle) {
        try {

            JobDetail jobDetail = buildEndJobDetail(raffle);
            Trigger trigger = buildJobTrigger(raffle.getEndAt());

            scheduler.scheduleJob(jobDetail, trigger);

        } catch (SchedulerException e) {
            Throwable cause = e.getCause();  // 원인 예외 분석

            if (cause instanceof JobPersistenceException) {
                // JobPersistenceException: 작업을 저장할 수 없을 때 발생
                throw new CustomException(ErrorStatus.JOB_STORE_FAILED);
            } else if (cause instanceof JobExecutionException) {
                // JobExecutionException: 작업 실행에 문제가 있을 때 발생
                throw new CustomException(ErrorStatus.JOB_EXECUTION_FAILED);

//            } else if (cause instanceof JobInterruptException) {
//                // JobInterruptException: 작업이 인터럽트되었을 때 발생
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
        return TriggerBuilder.newTrigger()
                .startAt(Date.from(time.atZone(java.time.ZoneId.systemDefault()).toInstant()))
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionFireNow())
                .build();
    }
}

