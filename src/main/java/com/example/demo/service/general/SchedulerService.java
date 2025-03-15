package com.example.demo.service.general;

import com.example.demo.entity.Delivery;
import com.example.demo.entity.Raffle;

public interface SchedulerService {
    void scheduleRaffleJob(Raffle raffle);

    void scheduleDrawJob(Raffle raffle);

    void cancelDrawJob(Raffle raffle);

    void scheduleDeliveryJob(Delivery delivery);

    void cancelDeliveryJob(Delivery delivery, String type);
}
