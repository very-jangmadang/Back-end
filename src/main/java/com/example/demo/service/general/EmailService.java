package com.example.demo.service.general;

import com.example.demo.entity.Delivery;
import com.example.demo.entity.Raffle;
import com.example.demo.entity.User;

public interface EmailService {

    void sendWinnerPrizeEmail(Delivery delivery);

    void sendWinnerCancelEmail(Delivery delivery);

    void sendOwnerAddressExpiredEmail(Delivery delivery);

    void sendOwnerCancelEmail(Raffle raffle);

    void sendWinnerShippingExpiredEmail(Delivery delivery);

    void sendOwnerUnfulfilledEmail(Raffle raffle);

    void sendOwnerReadyEmail(Delivery delivery);

}
