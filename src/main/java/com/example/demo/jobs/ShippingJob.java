package com.example.demo.jobs;

import com.example.demo.base.code.exception.CustomException;
import com.example.demo.base.status.ErrorStatus;
import com.example.demo.entity.Delivery;
import com.example.demo.entity.base.enums.DeliveryStatus;
import com.example.demo.repository.DeliveryRepository;
import com.example.demo.service.general.DeliverySchedulerService;
import com.example.demo.service.general.DeliveryService;
import com.example.demo.service.general.EmailService;
import lombok.RequiredArgsConstructor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ShippingJob implements Job {

    private final DeliveryRepository deliveryRepository;
    private final DeliverySchedulerService deliverySchedulerService;
    private final EmailService emailService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        Long deliveryId = context.getJobDetail().getJobDataMap().getLong("deliveryId");

        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new CustomException(ErrorStatus.DELIVERY_NOT_FOUND));

        delivery.setDeliveryStatus(DeliveryStatus.SHIPPING_EXPIRED);
        deliveryRepository.save(delivery);

        try {
            deliverySchedulerService.scheduleDeliveryJob(delivery);
        } catch (SchedulerException e) {
            throw new JobExecutionException(e, false);
        }

        emailService.sendWinnerShippingExpiredEmail(delivery);

    }
}
