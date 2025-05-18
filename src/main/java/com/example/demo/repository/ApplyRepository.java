package com.example.demo.repository;

import com.example.demo.entity.Apply;
import com.example.demo.entity.Raffle;
import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface ApplyRepository extends JpaRepository<Apply, Long> {
  
    boolean existsByRaffleAndUser(Raffle raffle, User user);

    @Query("SELECT a FROM Apply a JOIN FETCH a.raffle r WHERE a.user = :user")
    List<Apply> findWithRaffleByUser(@Param("user") User user);

    @Query("SELECT a.raffle.id, COUNT(a) FROM Apply a WHERE a.raffle.id IN :raffleIds GROUP BY a.raffle.id")
    List<Object[]> countAppliesByRaffleIds(@Param("raffleIds") List<Long> raffleIds);

    Apply findByRaffleAndUser(Raffle raffle, User user);

    @Modifying
    @Query("UPDATE Apply a SET a.isChecked = false WHERE a.raffle = :raffle")
    void updateUncheckedByRaffle(@Param("raffle") Raffle raffle);

    @Query("SELECT a.user FROM Apply a WHERE a.raffle = :raffle")
    List<User> findUsersByRaffle(@Param("raffle") Raffle raffle);

}
