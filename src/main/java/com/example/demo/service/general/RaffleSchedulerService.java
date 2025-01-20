package com.example.demo.service.general;

import com.example.demo.entity.Raffle;

public interface RaffleSchedulerService {

    public void scheduleRaffleStart(Raffle raffle);

    public void scheduleRaffleEnd(Raffle raffle);
}
