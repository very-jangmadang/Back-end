package com.example.demo.service.general;

import com.example.demo.entity.Raffle;

public interface RaffleSchedulerService {

    void scheduleRaffleJob(Raffle raffle, boolean isStart);
}
