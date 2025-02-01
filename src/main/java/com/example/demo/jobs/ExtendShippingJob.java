package com.example.demo.jobs;

import com.example.demo.base.code.exception.CustomException;
import com.example.demo.base.status.ErrorStatus;
import com.example.demo.entity.Delivery;
import com.example.demo.entity.Raffle;
import com.example.demo.entity.base.enums.DeliveryStatus;
import com.example.demo.repository.DeliveryRepository;
import com.example.demo.service.general.DrawService;
import com.example.demo.service.general.EmailService;
import lombok.RequiredArgsConstructor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExtendShippingJob implements Job {

    private final DeliveryRepository deliveryRepository;
    private final DrawService drawService;
    private final EmailService emailService;

    @Override
    public void execute(JobExecutionContext context) {
        Long deliveryId = context.getJobDetail().getJobDataMap().getLong("deliveryId");

        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new CustomException(ErrorStatus.DELIVERY_NOT_FOUND));

        delivery.setDeliveryStatus(DeliveryStatus.CANCELLED);
        deliveryRepository.save(delivery);

        Raffle raffle = delivery.getRaffle();
        drawService.cancel(raffle);

        emailService.sendWinnerCancelEmail(delivery);
        emailService.sendOwnerCancelEmail(raffle);

        // Todo: 배송비 환불

    }
}
