package com.example.demo.service.general;

import com.example.demo.entity.Raffle;

public interface RaffleSchedulerService {

    public void scheduleRaffleJob(Raffle raffle, boolean isStart);
}
