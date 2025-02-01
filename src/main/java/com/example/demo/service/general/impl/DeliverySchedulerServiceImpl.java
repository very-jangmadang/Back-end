package com.example.demo.service.general.impl;

import com.example.demo.base.Constants;
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
    public void scheduleDeliveryJob(Delivery delivery) throws SchedulerException {
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
        }

        scheduler.scheduleJob(jobDetail, trigger);
    }

    private JobDetail buildAddressJobDetail(Delivery delivery) {
        return JobBuilder.newJob(AddressJob.class)
                .withIdentity("Delivery_" + delivery.getId() + "_WAITING_ADDRESS")
                .usingJobData("deliveryId", delivery.getId())
                .storeDurably()
                .build();
    }

    private JobDetail buildShippingJobDetail(Delivery delivery) {
        return JobBuilder.newJob(ShippingJob.class)
                .withIdentity("Delivery_" + delivery.getId() + "_READY")
                .usingJobData("deliveryId", delivery.getId())
                .storeDurably()
                .build();
    }

    private JobDetail buildExtendAddressJobDetail(Delivery delivery) {
        return JobBuilder.newJob(ExtendAddressJob.class)
                .withIdentity("Delivery_" + delivery.getId() + "_ADDRESS_EXPIRED")
                .usingJobData("deliveryId", delivery.getId())
                .storeDurably()
                .build();
    }

    private JobDetail buildExtendShippingJobDetail(Delivery delivery) {
        return JobBuilder.newJob(ExtendShippingJob.class)
                .withIdentity("Delivery_" + delivery.getId() + "_SHIPPING_EXPIRED")
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
