package com.example.demo.service.general.impl;

import com.example.demo.base.code.exception.CustomException;
import com.example.demo.base.status.ErrorStatus;
import com.example.demo.domain.dto.Delivery.DeliveryRequestDTO;
import com.example.demo.domain.dto.Delivery.DeliveryResponseDTO;
import com.example.demo.domain.dto.MypageResponseDTO;
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
import java.util.ArrayList;
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

    @Override
    public DeliveryResponseDTO.DeliveryDto getDelivery(Long deliveryId) {
        // 사용자 정보 조회 (JWT 기반 인증 후 추후 구현 예정)
        User user = userRepository.findById(2L)
                .orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));

        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new CustomException(ErrorStatus.DELIVERY_NOT_FOUND));

        User winner = delivery.getWinner();
        if (user != winner)
            throw new CustomException(ErrorStatus.DELIVERY_FAIL);

        LocalDateTime now = LocalDateTime.now();
        if (delivery.getDeliveryStatus() == DeliveryStatus.ADDRESS_WAITING
                && now.isAfter(delivery.getAddressDeadline()))
            throw new CustomException(ErrorStatus.DELIVERY_ADDRESS_EXPIRED);

        List<Address> addressList = user.getAddresses();

        if (addressList.isEmpty())
            throw new CustomException(ErrorStatus.ADDRESS_NOT_FOUND);

        List<MypageResponseDTO.AddressDto> addressDtos = new ArrayList<>();
        for (Address address : addressList) {
            MypageResponseDTO.AddressDto addressDto = toAddressDto(address);
            addressDtos.add(addressDto);
        }

        return toDeliveryDto(delivery, addressDtos);
    }

    @Override
    @Transactional
    public DeliveryResponseDTO.AddressChoiceDto chooseAddress(
            Long deliveryId, DeliveryRequestDTO.WinnerDTO winnerDTO) {
        // 사용자 정보 조회 (JWT 기반 인증 후 추후 구현 예정)
        User user = userRepository.findById(2L)
                .orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));

        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new CustomException(ErrorStatus.DELIVERY_NOT_FOUND));

        User winner = delivery.getWinner();
        if (user != winner)
            throw new CustomException(ErrorStatus.DELIVERY_FAIL);

        Long addressId = winnerDTO.getAddressId();

        Address address = addressRepository.findByIdAndUser(addressId, user)
                .orElseThrow(() -> new CustomException(ErrorStatus.ADDRESS_MISMATCH_USER));

        LocalDateTime now = LocalDateTime.now();
        if (delivery.getDeliveryStatus() == DeliveryStatus.ADDRESS_WAITING
                && now.isAfter(delivery.getAddressDeadline()))
            throw new CustomException(ErrorStatus.DELIVERY_ADDRESS_EXPIRED);

        LocalDateTime deadline = LocalDateTime.now().withSecond(0).withNano(0).plusHours(96);

        delivery.setAddress(address);
        delivery.setDeliveryStatus(DeliveryStatus.READY);
        delivery.setShippingDeadline(deadline);
        deliveryRepository.save(delivery);

        return toAddressChoiceDto(delivery);
    }

    @Override
    public DeliveryResponseDTO.ResultDto getResult(Long deliveryId) {
        // 사용자 정보 조회 (JWT 기반 인증 후 추후 구현 예정)
        User user = userRepository.findById(2L)
                .orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));

        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new CustomException(ErrorStatus.DELIVERY_NOT_FOUND));

        if (user != delivery.getUser())
            throw new CustomException(ErrorStatus.DELIVERY_NOT_OWNER);

        LocalDateTime now = LocalDateTime.now();
        if (delivery.getDeliveryStatus() == DeliveryStatus.READY
                && now.isAfter(delivery.getShippingDeadline()))
            throw new CustomException(ErrorStatus.DELIVERY_SHIPPING_EXPIRED);

        Raffle raffle = delivery.getRaffle();

        int applyNum = applyRepository.countByRaffle(raffle);
        int ticket = raffle.getTicketNum();

        return toResultDto(delivery, applyNum * ticket);

    }

    @Override
    @Transactional
    public DeliveryResponseDTO.ShippingDto addInvoice(
            Long deliveryId, DeliveryRequestDTO.OwnerDTO ownerDTO) {

        // 사용자 정보 조회 (JWT 기반 인증 후 추후 구현 예정)
        User user = userRepository.findById(2L)
                .orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));

        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new CustomException(ErrorStatus.DELIVERY_NOT_FOUND));

        if (user != delivery.getUser())
            throw new CustomException(ErrorStatus.DELIVERY_NOT_OWNER);

        DeliveryStatus deliveryStatus = delivery.getDeliveryStatus();

        LocalDateTime now = LocalDateTime.now();
        if (deliveryStatus == DeliveryStatus.READY
                && now.isAfter(delivery.getShippingDeadline()))
            throw new CustomException(ErrorStatus.DELIVERY_SHIPPING_EXPIRED);

        if (deliveryStatus == DeliveryStatus.ADDRESS_WAITING)
            throw new CustomException(ErrorStatus.DELIVERY_BEFORE_ADDRESS);

        if (now.isAfter(delivery.getAddressDeadline()))
            throw new CustomException(ErrorStatus.DELIVERY_ADDRESS_EXPIRED);

        if (deliveryStatus == DeliveryStatus.SHIPPED)
            throw new CustomException(ErrorStatus.DELIVERY_ALREADY_SHIPPED);

        delivery.setCourierCompany(ownerDTO.getCourierCompany());
        delivery.setInvoiceNumber(ownerDTO.getInvoiceNumber());
        delivery.setDeliveryStatus(DeliveryStatus.SHIPPED);
        deliveryRepository.save(delivery);

        return toShippingDto(delivery);
    }

    @Override
    public DeliveryResponseDTO.ResultDto waitAddress(Long deliveryId) {
        // 사용자 정보 조회 (JWT 기반 인증 후 추후 구현 예정)
        User user = userRepository.findById(2L)
                .orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));

        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new CustomException(ErrorStatus.DELIVERY_NOT_FOUND));

        if (user != delivery.getUser())
            throw new CustomException(ErrorStatus.DELIVERY_NOT_OWNER);

        DeliveryStatus deliveryStatus = delivery.getDeliveryStatus();

        if (deliveryStatus == DeliveryStatus.READY)
            throw new CustomException(ErrorStatus.DELIVERY_ALREADY_READY);

        LocalDateTime deadline = delivery.getAddressDeadline().plusHours(24);
        delivery.setAddressDeadline(deadline);
        deliveryRepository.save(delivery);

        Raffle raffle = delivery.getRaffle();
        int applyNum = applyRepository.countByRaffle(raffle);
        int ticket = raffle.getTicketNum();

        return toResultDto(delivery, applyNum * ticket);
    }
}
