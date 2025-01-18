package com.example.demo.repository;

import com.example.demo.entity.Inquiry;
import com.example.demo.entity.Raffle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InquiryRepository extends JpaRepository<Inquiry, Long> {
    List<Inquiry> findAllByRaffle(Raffle raffle);
}

