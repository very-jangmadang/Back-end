package com.example.demo.service.general;

import com.example.demo.entity.Raffle;
import org.quartz.SchedulerException;

public interface DrawSchedulerService {

    void scheduleDrawJob(Raffle raffle);

    void cancelDrawJob(Raffle raffle);
}
