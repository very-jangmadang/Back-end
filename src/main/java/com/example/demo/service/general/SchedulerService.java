package com.example.demo.service.general;

import com.example.demo.domain.dto.Scheduler.SchedulerResponseDTO;
import com.example.demo.entity.Delivery;
import com.example.demo.entity.Raffle;

public interface SchedulerService {
    void scheduleRaffleJob(Raffle raffle);

    void scheduleDrawJob(Raffle raffle);

    void cancelDrawJob(Raffle raffle);

    void scheduleDeliveryJob(Delivery delivery);

    void cancelDeliveryJob(Delivery delivery, String type);

    void cancelRaffleJob(Raffle raffle);

    SchedulerResponseDTO getJobKeys();

    void scheduleAll();

    void scheduleNew(Long raffleId);

    void scheduleInvoiceCheckJob(Delivery delivery);

    void scheduleAddressCheckJob(Delivery delivery);
}
