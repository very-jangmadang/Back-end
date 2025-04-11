package com.example.demo.jobs;

import com.example.demo.base.code.exception.CustomException;
import com.example.demo.base.status.ErrorStatus;
import com.example.demo.entity.Delivery;
import com.example.demo.entity.base.enums.DeliveryStatus;
import com.example.demo.repository.DeliveryRepository;
import com.example.demo.service.general.EmailService;
import com.example.demo.service.general.NotificationService;
import com.example.demo.service.general.SchedulerService;
import lombok.RequiredArgsConstructor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AddressJob implements Job {

    private final DeliveryRepository deliveryRepository;
    private final SchedulerService schedulerService;
    private final EmailService emailService;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public void execute(JobExecutionContext context){
        Long deliveryId = context.getJobDetail().getJobDataMap().getLong("deliveryId");

        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new CustomException(ErrorStatus.DELIVERY_NOT_FOUND));

        delivery.setDeliveryStatus(DeliveryStatus.ADDRESS_EXPIRED);

        // TODO: 이메일 내용에 따라 함수 분리 가능성 있음
        emailService.sendOwnerAddressExpiredEmail(delivery);
        notificationService.sendHostForUnenteredAddress(delivery);
        schedulerService.scheduleDeliveryJob(delivery);
    }
}
