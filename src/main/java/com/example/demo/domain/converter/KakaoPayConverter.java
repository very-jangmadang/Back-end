package com.example.demo.domain.converter;

import com.example.demo.domain.dto.Payment.PaymentRequest;
import com.example.demo.domain.dto.Payment.ReadyResponse;
import com.example.demo.entity.Payment.Payment;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Getter
@Component
public class KakaoPayConverter {

    private final String cid;
    private final String approvalUrl;
    private final String cancelUrl;
    private final String failUrl;
    private final UserRepository userRepository;

    @Autowired
    public KakaoPayConverter(
            @Value("${kakao.pay.cid}") String cid,
            @Value("${kakao.pay.approvalUrl}") String approvalUrl,
            @Value("${kakao.pay.cancelUrl}") String cancelUrl,
            @Value("${kakao.pay.failUrl}") String failUrl,
            UserRepository userRepository) {
        this.cid = cid;
        this.approvalUrl = approvalUrl;
        this.cancelUrl = cancelUrl;
        this.failUrl = failUrl;
        this.userRepository = userRepository;
    }


    public Map<String, Object> toPrepareParameters(PaymentRequest paymentRequest) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("cid", this.cid);
        parameters.put("partner_user_id", paymentRequest.getUserId());
        parameters.put("partner_order_id", paymentRequest.getOrderId());
        parameters.put("item_name", paymentRequest.getItemName());
        parameters.put("quantity", paymentRequest.getQuantity());
        parameters.put("total_amount", paymentRequest.getTotalAmount());
        parameters.put("tax_free_amount", 0);
        parameters.put("approval_url", this.approvalUrl);
        parameters.put("cancel_url", this.cancelUrl);
        parameters.put("fail_url", this.failUrl);
        return parameters;
    }

    public Map<String, Object> toApproveParameters(Payment payment, String pgToken) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("cid", this.cid);
        parameters.put("tid", payment.getTid());
        parameters.put("partner_user_id", payment.getUser().getId());
        parameters.put("partner_order_id", payment.getOrderId());
        parameters.put("pg_token", pgToken);
        return parameters;
    }

    public Payment toEntity(User user, PaymentRequest paymentRequest, ReadyResponse readyResponse) {
        Payment payment = new Payment();
        payment.setUser(user);
        payment.setOrderId(paymentRequest.getOrderId());
        payment.setItemId(paymentRequest.getItemId());
        payment.setItemName(paymentRequest.getItemName());
        payment.setQuantity(paymentRequest.getQuantity());
        payment.setAmount(paymentRequest.getTotalAmount());
        payment.setTid(readyResponse.getTid());
        payment.setStatus("READY");
        payment.setCreatedAt(LocalDateTime.now());
        return payment;
    }

    public static PaymentRequest toPaymentRequest(Long userId, String itemId, String itemName, int totalAmount) {
        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setUserId(userId);
        paymentRequest.setOrderId("order_" + System.currentTimeMillis());
        paymentRequest.setItemId(itemId);
        paymentRequest.setItemName(itemName);
        paymentRequest.setTotalAmount(totalAmount);
        paymentRequest.setTaxFreeAmount(0);
        return paymentRequest;
    }

    public Map<String, Object> toCancelParameters(Payment payment) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("cid", this.cid);
        parameters.put("tid", payment.getTid());
        parameters.put("cancel_amount", payment.getAmount());
        parameters.put("cancel_tax_free_amount", 0);
        return parameters;
    }
} 
