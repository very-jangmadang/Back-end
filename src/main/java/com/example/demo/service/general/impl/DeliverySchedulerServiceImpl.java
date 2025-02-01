package com.example.demo.service.general.impl;

import com.example.demo.base.Constants;
import com.example.demo.base.code.exception.CustomException;
import com.example.demo.base.status.ErrorStatus;
import com.example.demo.entity.Delivery;
import com.example.demo.entity.base.enums.DeliveryStatus;
import com.example.demo.jobs.*;
import com.example.demo.service.general.DeliverySchedulerService;
import lombok.RequiredArgsConstructor;
import org.quartz.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class DeliverySchedulerServiceImpl implements DeliverySchedulerService {

    private final Scheduler scheduler;

    @Override
    public void scheduleDeliveryJob(Delivery delivery) {
        try {
            if (!scheduler.isStarted()) {
                scheduler.start();
            }

            JobDetail jobDetail = null;
            Trigger trigger = null;

            DeliveryStatus deliveryStatus = delivery.getDeliveryStatus();
            switch (deliveryStatus) {
                case WAITING_ADDRESS:
                    jobDetail = buildAddressJobDetail(delivery);
                    trigger = buildJobTrigger(delivery.getAddressDeadline());
                    break;
                case READY:
                    jobDetail = buildShippingJobDetail(delivery);
                    trigger = buildJobTrigger(delivery.getShippingDeadline());
                    break;
                case ADDRESS_EXPIRED:
                    jobDetail = buildExtendAddressJobDetail(delivery);
                    trigger = buildJobTrigger(delivery.getAddressDeadline().plusHours(Constants.WAIT));
                    break;
                case SHIPPING_EXPIRED:
                    jobDetail = buildExtendShippingJobDetail(delivery);
                    trigger = buildJobTrigger(delivery.getShippingDeadline().plusHours(Constants.WAIT));
                    break;
                default:
                    throw new CustomException(ErrorStatus.INVALID_DELIVERY_STATUS);
            }

            if (jobDetail == null || trigger == null)
                throw new CustomException(ErrorStatus.JOB_CREATION_FAILED);

            scheduler.scheduleJob(jobDetail, trigger);

        } catch (SchedulerException e) {
            Throwable cause = e.getCause();

            if (cause instanceof JobPersistenceException) {
                // JobPersistenceException: 작업을 저장할 수 없을 때 발생
                throw new CustomException(ErrorStatus.JOB_STORE_FAILED);
            } else if (cause instanceof JobExecutionException) {
                // JobExecutionException: 작업 실행에 문제가 있을 때 발생
                throw new CustomException(ErrorStatus.JOB_EXECUTION_FAILED);
            } else {
                // 그 외 다른 오류들
                throw new CustomException(ErrorStatus.JOB_UNKNOWN);
            }
        }
    }

    @Override
    public void cancelDeliveryJob(Delivery delivery) {
        try {
            String jobName = "Delivery_" + delivery.getId();
            JobKey jobKey = JobKey.jobKey(jobName);
            TriggerKey triggerKey = TriggerKey.triggerKey(jobName);

            if (scheduler.checkExists(jobKey)) {
                scheduler.unscheduleJob(triggerKey);
                scheduler.deleteJob(jobKey);
            }
            else
                throw new CustomException(ErrorStatus.JOB_NOT_FOUND);

        } catch (SchedulerException e) {
            throw new CustomException(ErrorStatus.JOB_CANCEL_FAILED);
        }
    }

    private JobDetail buildAddressJobDetail(Delivery delivery) {
        return JobBuilder.newJob(AddressJob.class)
                .withIdentity("Delivery_" + delivery.getId())
                .usingJobData("deliveryId", delivery.getId())
                .storeDurably()
                .build();
    }

    private JobDetail buildShippingJobDetail(Delivery delivery) {
        return JobBuilder.newJob(ShippingJob.class)
                .withIdentity("Delivery_" + delivery.getId())
                .usingJobData("deliveryId", delivery.getId())
                .storeDurably()
                .build();
    }

    private JobDetail buildExtendAddressJobDetail(Delivery delivery) {
        return JobBuilder.newJob(ExtendAddressJob.class)
                .withIdentity("Delivery_" + delivery.getId())
                .usingJobData("deliveryId", delivery.getId())
                .storeDurably()
                .build();
    }

    private JobDetail buildExtendShippingJobDetail(Delivery delivery) {
        return JobBuilder.newJob(ExtendShippingJob.class)
                .withIdentity("Delivery_" + delivery.getId())
                .usingJobData("deliveryId", delivery.getId())
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
