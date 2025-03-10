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
import lombok.RequiredArgsConstructor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ExtendShippingJob implements Job {

    private final DeliveryRepository deliveryRepository;
    private final DrawService drawService;
    private final EmailService emailService;
    private final KakaoPayService kakaoPayService;
    private final BaseController baseController;

    @Override
    @Transactional
    public void execute(JobExecutionContext context) {
        Long deliveryId = context.getJobDetail().getJobDataMap().getLong("deliveryId");

        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new CustomException(ErrorStatus.DELIVERY_NOT_FOUND));

        // Todo: Yoon 시작 - 배송비 환불

        Long userId = baseController.getCurrentUserId();
        kakaoPayService.cancelPayment(userId);

        // Todo: Yoon 완료

        delivery.setDeliveryStatus(DeliveryStatus.CANCELLED);
        deliveryRepository.save(delivery);

        Raffle raffle = delivery.getRaffle();
        drawService.cancel(raffle);

        emailService.sendWinnerCancelEmail(delivery);
        emailService.sendOwnerCancelEmail(raffle);

    }
}
