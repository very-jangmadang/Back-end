package com.example.demo.repository;

import com.example.demo.entity.Apply;
import com.example.demo.entity.Raffle;
import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApplyRepository extends JpaRepository<Apply, Long> {

    int countByRaffle(Raffle raffle);

    List<Apply> findByRaffle(Raffle raffle);
  
    boolean existsByRaffleAndUser(Raffle raffle, User user);

}
