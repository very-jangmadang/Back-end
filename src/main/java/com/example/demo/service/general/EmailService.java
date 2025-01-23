package com.example.demo.service.general;

import com.example.demo.entity.Raffle;
import com.example.demo.entity.User;

public interface EmailService {

    public void sendEmail(User user, Raffle raffle);
}
