package com.example.demo.service.general;

import com.example.demo.entity.Delivery;

public interface DeliverySchedulerService {

    void scheduleDeliveryJob(Delivery delivery);

    void cancelDeliveryJob(Delivery delivery, String type);
}
