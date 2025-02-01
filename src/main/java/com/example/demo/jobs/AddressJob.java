package com.example.demo.jobs;

import com.example.demo.base.code.exception.CustomException;
import com.example.demo.base.status.ErrorStatus;
import com.example.demo.entity.Delivery;
import com.example.demo.entity.Raffle;
import com.example.demo.entity.base.enums.DeliveryStatus;
import com.example.demo.repository.DeliveryRepository;
import com.example.demo.service.general.DeliverySchedulerService;
import com.example.demo.service.general.DrawService;
import com.example.demo.service.general.EmailService;
import lombok.RequiredArgsConstructor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AddressJob implements Job {

    private final DeliveryRepository deliveryRepository;
    private final DeliverySchedulerService deliverySchedulerService;
    private final EmailService emailService;
    private final DrawService drawService;

    @Override
    @Transactional
    public void execute(JobExecutionContext context){
        Long deliveryId = Long.parseLong(context.getJobDetail().getJobDataMap().getString("deliveryId"));

        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new CustomException(ErrorStatus.DELIVERY_NOT_FOUND));

        if (!delivery.isAddressExtended()) {
            delivery.setDeliveryStatus(DeliveryStatus.ADDRESS_EXPIRED);
            deliveryRepository.save(delivery);

            deliverySchedulerService.scheduleDeliveryJob(delivery);
            emailService.sendOwnerAddressExpiredEmail(delivery);
        } else {
            delivery.setDeliveryStatus(DeliveryStatus.CANCELLED);
            deliveryRepository.save(delivery);

            Raffle raffle = delivery.getRaffle();
            drawService.cancel(raffle);

            emailService.sendWinnerCancelEmail(delivery);
            emailService.sendOwnerCancelEmail(raffle);
        }
    }
}
