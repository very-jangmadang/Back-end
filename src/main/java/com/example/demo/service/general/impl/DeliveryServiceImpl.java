package com.example.demo.service.general.impl;

import com.example.demo.base.Constants;
import com.example.demo.base.code.exception.CustomException;
import com.example.demo.base.status.ErrorStatus;
import com.example.demo.domain.dto.Delivery.DeliveryRequestDTO;
import com.example.demo.domain.dto.Delivery.DeliveryResponseDTO;
import com.example.demo.domain.dto.Mypage.MypageResponseDTO;
import com.example.demo.entity.Address;
import com.example.demo.entity.Delivery;
import com.example.demo.entity.Raffle;
import com.example.demo.entity.User;
import com.example.demo.entity.base.enums.DeliveryStatus;
import com.example.demo.repository.*;
import com.example.demo.service.general.DeliveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.example.demo.domain.converter.DeliveryConverter.*;
import static com.example.demo.domain.converter.MypageConverter.toAddressDto;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DeliveryServiceImpl implements DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final ApplyRepository applyRepository;

    private User getUser() {
        // 사용자 정보 조회 (JWT 기반 인증 후 추후 구현 예정)
        return userRepository.findById(2L)
                .orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));
    }

    private Delivery getDeliveryById(Long deliveryId) {
        return deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new CustomException(ErrorStatus.DELIVERY_NOT_FOUND));
    }

    private void updateDeliveryStatus(Delivery delivery, DeliveryStatus status) {
        delivery.setDeliveryStatus(status);
        deliveryRepository.save(delivery);
    }

    @Override
    public DeliveryResponseDTO.DeliveryDto getDelivery(Long deliveryId) {
        User user = getUser();
        Delivery delivery = getDeliveryById(deliveryId);

        if (!user.equals(delivery.getWinner()))
            throw new CustomException(ErrorStatus.DELIVERY_NOT_WINNER);


        DeliveryStatus deliveryStatus = delivery.getDeliveryStatus();
        if (deliveryStatus == DeliveryStatus.WAITING_ADDRESS
                || deliveryStatus == DeliveryStatus.WAITING_PAYMENT)
            return toDeliveryDto(delivery, null);

        MypageResponseDTO.AddressDto addressDto = toAddressDto(delivery.getAddress());
        return toDeliveryDto(delivery, addressDto);
    }

    @Override
    @Transactional
    public DeliveryResponseDTO.ResponseDto setAddress(Long deliveryId) {
        User user = getUser();
        Delivery delivery = getDeliveryById(deliveryId);

        if (!user.equals(delivery.getWinner()))
            throw new CustomException(ErrorStatus.DELIVERY_NOT_WINNER);

        DeliveryStatus deliveryStatus = delivery.getDeliveryStatus();
        switch (deliveryStatus){
            case CANCELLED:
                throw new CustomException(ErrorStatus.DELIVERY_FAIL);
            case ADDRESS_EXPIRED:
                throw new CustomException(ErrorStatus.DELIVERY_ADDRESS_EXPIRED);
        }

        if (deliveryStatus != DeliveryStatus.WAITING_ADDRESS
                && deliveryStatus != DeliveryStatus.WAITING_PAYMENT)
            throw new CustomException(ErrorStatus.DELIVERY_ALREADY_READY);

        List<Address> addressList = user.getAddresses();
        if (addressList.isEmpty())
            throw new CustomException(ErrorStatus.ADDRESS_EMPTY);

        Address defaultAddress = addressList.stream()
                .filter(Address::isDefault)
                .findFirst()
                .orElseThrow(() -> new CustomException(ErrorStatus.DELIVERY_NO_DEFAULT_ADDRESS));

        delivery.setAddress(defaultAddress);
        delivery.setDeliveryStatus(DeliveryStatus.WAITING_PAYMENT);
        deliveryRepository.save(delivery);

        return DeliveryResponseDTO.ResponseDto.builder()
                .deliveryId(deliveryId)
                .build();
    }

    @Override
    @Transactional
    public DeliveryResponseDTO.ResponseDto complete(Long deliveryId) {
        User user = getUser();
        Delivery delivery = getDeliveryById(deliveryId);

        if (!user.equals(delivery.getWinner()))
            throw new CustomException(ErrorStatus.DELIVERY_NOT_WINNER);

        DeliveryStatus deliveryStatus = delivery.getDeliveryStatus();
        switch (deliveryStatus){
            case CANCELLED:
                throw new CustomException(ErrorStatus.DELIVERY_FAIL);
            case ADDRESS_EXPIRED:
                throw new CustomException(ErrorStatus.DELIVERY_ADDRESS_EXPIRED);
            case WAITING_ADDRESS:
                throw new CustomException(ErrorStatus.DELIVERY_BEFORE_ADDRESS);
        }

        if (deliveryStatus != DeliveryStatus.WAITING_PAYMENT)
            throw new CustomException(ErrorStatus.DELIVERY_ALREADY_READY);

        delivery.setDeliveryStatus(DeliveryStatus.READY);
        delivery.setShippingDeadline(LocalDateTime.now()
                .plusHours(Constants.SHIPPING_DEADLINE)
                .withSecond(0)
                .withNano(0));
        deliveryRepository.save(delivery);

        return DeliveryResponseDTO.ResponseDto.builder()
                .deliveryId(deliveryId)
                .build();
    }

    @Override
    @Transactional
    public DeliveryResponseDTO.WaitDto waitShipping(Long deliveryId) {
        User user = getUser();
        Delivery delivery = getDeliveryById(deliveryId);

        if (!user.equals(delivery.getWinner()))
            throw new CustomException(ErrorStatus.DELIVERY_NOT_WINNER);

        DeliveryStatus deliveryStatus = delivery.getDeliveryStatus();
        switch (deliveryStatus) {
            case WAITING_ADDRESS:
            case WAITING_PAYMENT:
                throw new CustomException(ErrorStatus.DELIVERY_BEFORE_ADDRESS);
            case ADDRESS_EXPIRED:
                throw new CustomException(ErrorStatus.DELIVERY_ADDRESS_EXPIRED);
            case READY:
                throw new CustomException(ErrorStatus.DELIVERY_SHIPPING_NOT_EXPIRED);
            case SHIPPED:
                throw new CustomException(ErrorStatus.DELIVERY_ALREADY_SHIPPED);
            case CANCELLED:
                throw new CustomException(ErrorStatus.DELIVERY_FAIL);
        }

        delivery.extendShippingDeadline();
        deliveryRepository.save(delivery);

        return toWaitDto(delivery);
    }

    @Override
    @Transactional(rollbackFor = CustomException.class)
    public DeliveryResponseDTO.ResultDto getResult(Long deliveryId) {
        User user = getUser();
        Delivery delivery = getDeliveryById(deliveryId);

        if (!user.equals(delivery.getUser()))
            throw new CustomException(ErrorStatus.DELIVERY_NOT_OWNER);

        LocalDateTime now = LocalDateTime.now();
        if (delivery.getDeliveryStatus() == DeliveryStatus.READY
                && now.isAfter(delivery.getShippingDeadline())) {
            updateDeliveryStatus(delivery, DeliveryStatus.SHIPPING_EXPIRED);
            throw new CustomException(ErrorStatus.DELIVERY_SHIPPING_EXPIRED);
        }

        Raffle raffle = delivery.getRaffle();
        int applyNum = applyRepository.countByRaffle(raffle);
        int ticket = raffle.getTicketNum();

        return toResultDto(delivery, applyNum * ticket);

    }

    @Override
    @Transactional(rollbackFor = CustomException.class)
    public DeliveryResponseDTO.ShippingDto addInvoice(
            Long deliveryId, DeliveryRequestDTO.OwnerDTO ownerDTO) {

        User user = getUser();
        Delivery delivery = getDeliveryById(deliveryId);

        if (!user.equals(delivery.getUser()))
            throw new CustomException(ErrorStatus.DELIVERY_NOT_OWNER);

        DeliveryStatus deliveryStatus = delivery.getDeliveryStatus();

        if (deliveryStatus == DeliveryStatus.WAITING_ADDRESS)
            throw new CustomException(ErrorStatus.DELIVERY_BEFORE_ADDRESS);

        LocalDateTime now = LocalDateTime.now();
        if (delivery.getDeliveryStatus() == DeliveryStatus.READY
                && now.isAfter(delivery.getShippingDeadline())) {
            updateDeliveryStatus(delivery, DeliveryStatus.SHIPPING_EXPIRED);
            throw new CustomException(ErrorStatus.DELIVERY_SHIPPING_EXPIRED);
        }

        if (deliveryStatus == DeliveryStatus.SHIPPED)
            throw new CustomException(ErrorStatus.DELIVERY_ALREADY_SHIPPED);

//        delivery.setCourierCompany(ownerDTO.getCourierCompany());
        delivery.setInvoiceNumber(ownerDTO.getInvoiceNumber());
        delivery.setDeliveryStatus(DeliveryStatus.SHIPPED);
        deliveryRepository.save(delivery);

        return toShippingDto(delivery);
    }

    @Override
    @Transactional(rollbackFor = CustomException.class)
    public void waitAddress(Long deliveryId) {
        User user = getUser();
        Delivery delivery = getDeliveryById(deliveryId);

        if (!user.equals(delivery.getUser()))
            throw new CustomException(ErrorStatus.DELIVERY_NOT_OWNER);

        DeliveryStatus deliveryStatus = delivery.getDeliveryStatus();

        if (deliveryStatus == DeliveryStatus.READY)
            throw new CustomException(ErrorStatus.DELIVERY_ALREADY_READY);

        if (deliveryStatus == DeliveryStatus.WAITING_ADDRESS)
            throw new CustomException(ErrorStatus.DELIVERY_ADDRESS_NOT_EXPIRED);

        LocalDateTime deadline = delivery.getAddressDeadline().plusHours(24);
        delivery.extendAddressDeadline();
        deliveryRepository.save(delivery);

    }
}
