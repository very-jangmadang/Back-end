package com.example.demo.jobs;

import com.example.demo.base.code.exception.CustomException;
import com.example.demo.base.status.ErrorStatus;
import com.example.demo.controller.BaseController;
import com.example.demo.entity.Delivery;
import com.example.demo.entity.Raffle;
import com.example.demo.entity.base.enums.DeliveryStatus;
import com.example.demo.repository.DeliveryRepository;
import com.example.demo.service.general.DrawService;
import com.example.demo.service.general.EmailService;
import com.example.demo.service.general.KakaoPayService;
import com.example.demo.service.general.NotificationService;
import lombok.RequiredArgsConstructor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class WaitingJob implements Job {

    private final DeliveryRepository deliveryRepository;
    private final DrawService drawService;
    private final EmailService emailService;
    private final NotificationService notificationService;
    private final KakaoPayService kakaoPayService;
    private final BaseController baseController;

    @Override
    @Transactional
    public void execute(JobExecutionContext context) {
        Long deliveryId = context.getJobDetail().getJobDataMap().getLong("deliveryId");

        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new CustomException(ErrorStatus.DELIVERY_NOT_FOUND));

        DeliveryStatus deliveryStatus = delivery.getDeliveryStatus();

        if (deliveryStatus == DeliveryStatus.SHIPPING_EXPIRED) {
            Long userId = baseController.getCurrentUserId();
            kakaoPayService.cancelPayment(userId);
        }

        delivery.setDeliveryStatus(DeliveryStatus.CANCELLED);
        emailService.sendWinnerCancelEmail(delivery);

        Raffle raffle = delivery.getRaffle();
        drawService.cancel(raffle);

        emailService.sendOwnerCancelEmail(raffle);

    }
}
