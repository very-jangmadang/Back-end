package com.example.demo.repository;

import com.example.demo.entity.Inquiry;
import com.example.demo.entity.Like;
import com.example.demo.entity.Raffle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InquiryRepository extends JpaRepository<Inquiry, Long> {

    List<Inquiry> findByRaffle(Raffle raffle);

}

