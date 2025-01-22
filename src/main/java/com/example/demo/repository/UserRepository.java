package com.example.demo.repository;

import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findById(Long id);

    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.ticket_num = u.ticket_num + :refundTicket WHERE u.id IN :userIds")
    void batchUpdateTicketNum(@Param("refundTicket") int refundTicket, @Param("userIds") List<Long> userIds);

}
