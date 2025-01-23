package com.example.demo.repository;

import com.example.demo.entity.Delivery;
import com.example.demo.entity.Raffle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {

    Optional<Delivery> findByRaffle(Raffle raffle);

}
