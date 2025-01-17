package com.example.demo.repository;

import com.example.demo.entity.Raffle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RaffleRepository extends JpaRepository<Raffle, Long> {
    Optional<Raffle> findById(Long id);
    List<Raffle> findByCategoryId(Long categoryId);

}
