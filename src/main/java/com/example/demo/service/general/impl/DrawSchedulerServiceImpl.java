package com.example.demo.service.general.impl;

import com.example.demo.base.Constants;
import com.example.demo.entity.Raffle;
import com.example.demo.jobs.ExtendShippingJob;
import com.example.demo.service.general.DrawSchedulerService;
import lombok.RequiredArgsConstructor;
import org.quartz.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class DrawSchedulerServiceImpl implements DrawSchedulerService {

    private final Scheduler scheduler;

    @Override
    public void scheduleDrawJob(Raffle raffle) throws SchedulerException {
        if (!scheduler.isStarted()) {
            scheduler.start();
        }

        JobDetail jobDetail = buildDrawJobDetail(raffle);
        Trigger trigger = buildJobTrigger(raffle.getEndAt().plusHours(Constants.WAIT));

        scheduler.scheduleJob(jobDetail, trigger);
    }

    private JobDetail buildDrawJobDetail(Raffle raffle) {
        return JobBuilder.newJob(ExtendShippingJob.class)
                .withIdentity("Raffle_" + raffle.getId() + "_UNFULFILLED")
                .usingJobData("raffleId", raffle.getId())
                .storeDurably()
                .build();
    }

    private Trigger buildJobTrigger(LocalDateTime time) {
        LocalDateTime adjustedTime = time.withSecond(0).withNano(0);
        Date startDate = Date.from(adjustedTime.atZone(ZoneId.systemDefault()).toInstant());

        return TriggerBuilder.newTrigger()
                .startAt(startDate)
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withMisfireHandlingInstructionFireNow())
                .build();
    }
}
