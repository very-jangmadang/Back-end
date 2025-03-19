package com.example.demo.repository;

import com.example.demo.entity.Payment.UserPayment;
import com.example.demo.entity.Raffle;
import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {

    @Modifying
    @Query("UPDATE User u SET u.ticket_num = u.ticket_num + :refundTicket WHERE u.id IN :userIds")
    void batchUpdateTicketNum(@Param("refundTicket") int refundTicket, @Param("userIds") List<Long> userIds);

    @Modifying
    @Query("UPDATE User u SET u.followerVisible = :isVisible WHERE u.id = :userId")
    void updateFollowerVisibility(@Param("userId") Long userId, @Param("isVisible") boolean isVisible);

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    @Query("SELECT u.id FROM User u WHERE u.email = :email")
    Long findIdByEmail(String email);

}
