package com.example.demo.repository;

import com.example.demo.entity.Apply;
import com.example.demo.entity.Raffle;
import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplyRepository extends JpaRepository<Apply, Long> {

    boolean existsByRaffleAndUser(Raffle raffle, User user);

}
