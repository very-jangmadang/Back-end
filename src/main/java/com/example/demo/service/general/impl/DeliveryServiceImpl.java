package com.example.demo.service.general.impl;

import com.example.demo.base.code.exception.CustomException;
import com.example.demo.base.status.ErrorStatus;
import com.example.demo.domain.converter.MypageConverter;
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

@Service
@RequiredArgsConstructor
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
    @Transactional(rollbackFor = CustomException.class)
    public DeliveryResponseDTO.DeliveryDto getDelivery(Long deliveryId) {
        User user = getUser();
        Delivery delivery = getDeliveryById(deliveryId);

        if (!user.equals(delivery.getWinner()))
            throw new CustomException(ErrorStatus.DELIVERY_FAIL);

        LocalDateTime now = LocalDateTime.now();
        if (delivery.getDeliveryStatus() == DeliveryStatus.WAITING_ADDRESS
                && now.isAfter(delivery.getAddressDeadline())) {
            updateDeliveryStatus(delivery, DeliveryStatus.ADDRESS_EXPIRED);
            throw new CustomException(ErrorStatus.DELIVERY_ADDRESS_EXPIRED);
        }

        List<Address> addressList = user.getAddresses();

        if (addressList.isEmpty())
            throw new CustomException(ErrorStatus.ADDRESS_NOT_FOUND);

        List<MypageResponseDTO.AddressDto> addressDtos = addressList.stream()
                .map(MypageConverter::toAddressDto)
                .toList();

        return toDeliveryDto(delivery, addressDtos);
    }

    @Override
    @Transactional(rollbackFor = CustomException.class)
    public DeliveryResponseDTO.AddressChoiceDto chooseAddress(
            Long deliveryId, DeliveryRequestDTO.WinnerDTO winnerDTO) {
        User user = getUser();
        Delivery delivery = getDeliveryById(deliveryId);

        if (!user.equals(delivery.getWinner()))
            throw new CustomException(ErrorStatus.DELIVERY_FAIL);

        Long addressId = winnerDTO.getAddressId();

        Address address = addressRepository.findByIdAndUser(addressId, user)
                .orElseThrow(() -> new CustomException(ErrorStatus.ADDRESS_MISMATCH_USER));

        LocalDateTime now = LocalDateTime.now();
        if (delivery.getDeliveryStatus() == DeliveryStatus.WAITING_ADDRESS
                && now.isAfter(delivery.getAddressDeadline())) {
            updateDeliveryStatus(delivery, DeliveryStatus.ADDRESS_EXPIRED);
            throw new CustomException(ErrorStatus.DELIVERY_ADDRESS_EXPIRED);
        }

        LocalDateTime deadline = LocalDateTime.now().withSecond(0).withNano(0).plusHours(96);

        delivery.setAddress(address);
        delivery.setDeliveryStatus(DeliveryStatus.READY);
        delivery.setShippingDeadline(deadline);
        deliveryRepository.save(delivery);

        return toAddressChoiceDto(delivery);
    }

    @Override
    @Transactional(rollbackFor = CustomException.class)
    public void waitShipping(Long deliveryId) {
        User user = getUser();
        Delivery delivery = getDeliveryById(deliveryId);

        if (!user.equals(delivery.getWinner()))
            throw new CustomException(ErrorStatus.DELIVERY_FAIL);

        DeliveryStatus deliveryStatus = delivery.getDeliveryStatus();

        if (deliveryStatus == DeliveryStatus.SHIPPED)
            throw new CustomException(ErrorStatus.DELIVERY_ALREADY_SHIPPED);

        if (deliveryStatus == DeliveryStatus.READY)
            throw new CustomException(ErrorStatus.DELIVERY_SHIPPING_NOT_EXPIRED);

        LocalDateTime deadline = delivery.getShippingDeadline().plusHours(24);
        delivery.setShippingDeadline(deadline);
        deliveryRepository.save(delivery);

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
        delivery.setAddressDeadline(deadline);
        deliveryRepository.save(delivery);

    }
}
