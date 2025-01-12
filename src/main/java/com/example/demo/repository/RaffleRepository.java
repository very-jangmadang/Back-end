package com.example.demo.repository;

import com.example.demo.entity.Raffle;
import org.springframework.data.jpa.repository.JpaRepository;


public interface RaffleRepository extends JpaRepository<Raffle, Long> {
}
