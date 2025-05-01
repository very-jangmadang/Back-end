package com.example.demo.jobs;

import lombok.RequiredArgsConstructor;
import org.quartz.Job;

import com.example.demo.base.code.exception.CustomException;
import com.example.demo.base.status.ErrorStatus;
import com.example.demo.entity.Delivery;
import com.example.demo.entity.base.enums.DeliveryStatus;
import com.example.demo.repository.DeliveryRepository;
import com.example.demo.service.general.NotificationService;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class InvoiceOverJob implements Job {

    private final DeliveryRepository deliveryRepository;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public void execute(JobExecutionContext context) {
        Long deliveryId = context.getJobDetail().getJobDataMap().getLong("deliveryId");

        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new CustomException(ErrorStatus.DELIVERY_NOT_FOUND));

        if (delivery.getDeliveryStatus() == DeliveryStatus.READY && delivery.getInvoiceNumber() == null) {
            notificationService.sendWinnerForUnenteredInvoice(delivery);
        }
    }
}
