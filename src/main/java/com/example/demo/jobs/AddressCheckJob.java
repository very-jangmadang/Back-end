package com.example.demo.jobs;

import com.example.demo.base.code.exception.CustomException;
import com.example.demo.base.status.ErrorStatus;
import com.example.demo.entity.Delivery;
import com.example.demo.entity.base.enums.DeliveryStatus;
import com.example.demo.repository.DeliveryRepository;
import com.example.demo.service.general.NotificationService;
import lombok.RequiredArgsConstructor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
//마감 1시간 전 알림용
public class AddressCheckJob implements Job {

    private final DeliveryRepository deliveryRepository;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public void execute(JobExecutionContext context) {
        Long deliveryId = context.getJobDetail().getJobDataMap().getLong("deliveryId");

        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new CustomException(ErrorStatus.DELIVERY_NOT_FOUND));

        if (delivery.getDeliveryStatus() == DeliveryStatus.WAITING_ADDRESS && delivery.getAddress() == null) {
            notificationService.sendWinnerForUnenteredAddress(delivery);
        }
    }
}