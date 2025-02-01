package com.example.demo.service.general;

import com.example.demo.entity.Delivery;
import org.quartz.SchedulerException;

public interface DeliverySchedulerService {

    void scheduleDeliveryJob(Delivery delivery);

    void cancelDeliveryJob(Delivery delivery);
}
