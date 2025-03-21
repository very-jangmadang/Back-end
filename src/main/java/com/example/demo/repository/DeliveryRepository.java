package com.example.demo.repository;

import com.example.demo.entity.Address;
import com.example.demo.entity.Delivery;
import com.example.demo.entity.Raffle;
import com.example.demo.entity.User;
import com.example.demo.entity.base.enums.DeliveryStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface DeliveryRepository extends JpaRepository<Delivery, Long> {

    boolean existsByAddressAndDeliveryStatusIn(Address address, List<DeliveryStatus> status);

    Delivery findByRaffleAndDeliveryStatusIn(Raffle raffle, List<DeliveryStatus> waitingAddress);
}
