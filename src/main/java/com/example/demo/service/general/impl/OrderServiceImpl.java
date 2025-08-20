package com.example.demo.service.general.impl;


import com.example.demo.domain.dto.Intent.OrderIntentResponseDTO;
import com.example.demo.entity.Payment.OrderIntent;
import com.example.demo.entity.base.enums.OrderStatus;
import com.example.demo.repository.OrderIntentRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.jwt.JWTUtil;
import io.jsonwebtoken.Jwt;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl {

    private final OrderIntentRepository orderIntentRepository;
    private final UserRepository userRepository;
    private final JWTUtil jwtUtil;

    // 1) 주문 의도 생성 + 토큰 발급
    @Transactional
    public OrderIntentResponseDTO.CreateIntentResponseDTO createIntent(Long userId, int qty) {
        String jti = UUID.randomUUID().toString();

        OrderIntent intent = new OrderIntent();
        intent.setUserId(userId);
        intent.setQuantity(qty);
        intent.setJti(jti);
//        intent.setStatus(OrderStatus.PEDDING);

        orderIntentRepository.save(intent);

        String token = jwtUtil.createOrderToken(userId, qty, jti);
        return new OrderIntentResponseDTO.CreateIntentResponseDTO(token);
    }
}
