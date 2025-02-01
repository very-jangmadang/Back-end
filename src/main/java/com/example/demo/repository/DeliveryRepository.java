package com.example.demo.repository;

import com.example.demo.entity.Address;
import com.example.demo.entity.Delivery;
import com.example.demo.entity.Raffle;
import com.example.demo.entity.User;
import com.example.demo.entity.base.enums.DeliveryStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface DeliveryRepository extends JpaRepository<Delivery, Long> {

    Delivery findByRaffleAndWinner(Raffle raffle, User winner);

    boolean existsByAddressAndDeliveryStatusIn(Address address, List<DeliveryStatus> status);
}
