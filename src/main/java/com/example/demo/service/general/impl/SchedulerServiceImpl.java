package com.example.demo.service.general.impl;

import com.example.demo.base.Constants;
import com.example.demo.base.code.exception.CustomException;
import com.example.demo.base.status.ErrorStatus;
import com.example.demo.entity.Delivery;
import com.example.demo.entity.Raffle;
import com.example.demo.jobs.*;
import com.example.demo.service.general.SchedulerService;
import lombok.RequiredArgsConstructor;
import org.quartz.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

@Service
@RequiredArgsConstructor
public class SchedulerServiceImpl implements SchedulerService {

    private final Scheduler scheduler;

    private void scheduleJob(String jobName, Class<? extends Job> jobClass, LocalDateTime time, Map<String, Object> jobData) {
        try {
            if (!scheduler.isStarted())
                scheduler.start();

            JobDetail jobDetail = buildJobDetail(jobName, jobClass, jobData);
            Trigger trigger = buildJobTrigger(time);

            scheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException e) {
            handleSchedulerException(e);
        }
    }

    private JobDetail buildJobDetail(String jobName, Class<? extends Job> jobClass, Map<String, Object> jobData) {
        JobDataMap dataMap = new JobDataMap();

        if (jobData != null)
            dataMap.putAll(jobData);

        return JobBuilder.newJob(jobClass)
                .withIdentity(jobName)
                .usingJobData(dataMap)
                .storeDurably()
                .build();
    }

    private Trigger buildJobTrigger(LocalDateTime time) {
        ZoneId appZoneId = TimeZone.getDefault().toZoneId();

        LocalDateTime adjustedTime = time.withSecond(0).withNano(0);
        Date startDate = Date.from(adjustedTime.atZone(appZoneId).toInstant());

        return TriggerBuilder.newTrigger()
                .startAt(startDate)
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withMisfireHandlingInstructionFireNow())
                .build();
    }

    private void handleSchedulerException(SchedulerException e) {
        Throwable cause = e.getCause();
        if (cause instanceof JobPersistenceException) {
            throw new CustomException(ErrorStatus.JOB_STORE_FAILED);
        } else if (cause instanceof JobExecutionException) {
            throw new CustomException(ErrorStatus.JOB_EXECUTION_FAILED);
        } else {
            throw new CustomException(ErrorStatus.JOB_UNKNOWN);
        }
    }

    @Override
    public void scheduleRaffleJob(Raffle raffle) {
        String startJobName = "Raffle_" + raffle.getId() + "_START";
        LocalDateTime startTime = raffle.getStartAt();
        scheduleJob(startJobName, RaffleStartJob.class, startTime, Map.of("raffleId", raffle.getId()));

        String endJobName = "Raffle_" + raffle.getId() + "_END";
        LocalDateTime endTime = raffle.getEndAt();
        scheduleJob(endJobName, RaffleEndJob.class, endTime, Map.of("raffleId", raffle.getId()));

    }

    @Override
    public void scheduleDrawJob(Raffle raffle) {
        String jobName = "Raffle_" + raffle.getId() + "_DRAW";
        scheduleJob(jobName, DrawJob.class, raffle.getEndAt().plusHours(Constants.DRAW_DEADLINE), Map.of("raffleId", raffle.getId()));
    }

    @Override
    public void scheduleDeliveryJob(Delivery delivery) {
        String jobName = "Delivery_" + delivery.getId();
        LocalDateTime triggerTime;
        Class<? extends Job> jobClass;

        switch (delivery.getDeliveryStatus()) {
            case WAITING_ADDRESS:
                jobName += "_Address";
                jobClass = AddressJob.class;
                triggerTime = delivery.getAddressDeadline();
                break;
            case READY:
                jobName += "_Shipping";
                jobClass = ShippingJob.class;
                triggerTime = delivery.getShippingDeadline();
                break;
            case SHIPPED:
                jobName += "_Complete";
                jobClass = CompleteJob.class;
                triggerTime = LocalDateTime.now().plusHours(Constants.COMPLETE);
                break;
            case ADDRESS_EXPIRED:
                jobName += "_Waiting";
                jobClass = WaitingJob.class;

                if (!delivery.isAddressExtended())
                    triggerTime = delivery.getAddressDeadline().plusHours(Constants.CHOICE_PERIOD);
                else
                    triggerTime = LocalDateTime.now().plusHours(Constants.CHOICE_PERIOD);

                break;
            case SHIPPING_EXPIRED:
                jobName += "_Waiting";
                jobClass = WaitingJob.class;
                triggerTime = delivery.getShippingDeadline().plusHours(Constants.CHOICE_PERIOD);
                break;
            default:
                throw new CustomException(ErrorStatus.INVALID_DELIVERY_STATUS);
        }

        scheduleJob(jobName, jobClass, triggerTime, Map.of("deliveryId", delivery.getId()));
    }

    public void cancelJob(String jobName) {
        try {
            JobKey jobKey = JobKey.jobKey(jobName);
            TriggerKey triggerKey = TriggerKey.triggerKey(jobName);

            if (scheduler.checkExists(jobKey)) {
                scheduler.unscheduleJob(triggerKey);
                scheduler.deleteJob(jobKey);
            }
//            else
//                throw new CustomException(ErrorStatus.JOB_NOT_FOUND);

        } catch (SchedulerException e) {
            throw new CustomException(ErrorStatus.JOB_CANCEL_FAILED);
        }
    }

    @Override
    public void cancelDrawJob(Raffle raffle) {
        cancelJob("Raffle_" + raffle.getId() + "_DRAW");
    }

    @Override
    public void cancelDeliveryJob(Delivery delivery, String type) {
        cancelJob("Delivery_" + delivery.getId() + "_" + type);
    }

    @Override
    public void cancelRaffleJob(Raffle raffle, boolean isStart) {
        String jobName = "Raffle_" + raffle.getId();
        jobName = isStart ? jobName + "_START" : jobName + "_END";
        cancelJob(jobName);
    }
}
