package com.example.demo.service.general.impl;

import com.example.demo.base.code.exception.CustomException;
import com.example.demo.base.status.ErrorStatus;
import com.example.demo.controller.BaseController;
import com.example.demo.domain.dto.Delivery.DeliveryRequestDTO;
import com.example.demo.domain.dto.Delivery.DeliveryResponseDTO;
import com.example.demo.domain.dto.Mypage.MypageResponseDTO;
import com.example.demo.entity.*;
import com.example.demo.entity.base.enums.DeliveryStatus;
import com.example.demo.entity.base.enums.RaffleStatus;
import com.example.demo.repository.*;
import com.example.demo.service.general.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.example.demo.domain.converter.DeliveryConverter.*;
import static com.example.demo.domain.converter.MypageConverter.toAddressDto;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DeliveryServiceImpl implements DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final UserRepository userRepository;
    private final DrawService drawService;
    private final SchedulerService schedulerService;
    private final EmailService emailService;
    private final KakaoPayService kakaoPayService;
    private final BaseController baseController;
    private final CourierRepository courierRepository;
    private final NotificationService notificationService;

    @Override
    public DeliveryResponseDTO.WinnerResultDto getDelivery(Long deliveryId) {
        User user = getUser();
        Delivery delivery = getDeliveryById(deliveryId);
        validateWinner(delivery, user);

        MypageResponseDTO.AddressDto addressDto;

        DeliveryStatus deliveryStatus = delivery.getDeliveryStatus();
        if (deliveryStatus == DeliveryStatus.WAITING_ADDRESS) {

            if (!user.getAddresses().isEmpty()) {
                Address defaultAddress = user.getAddresses().stream()
                        .filter(Address::isDefault)
                        .findFirst()
                        .orElseThrow(() -> new CustomException(ErrorStatus.DELIVERY_NO_ADDRESS));

                addressDto = toAddressDto(defaultAddress);
            } else
                addressDto = null;

        } else
            addressDto = toAddressDto(delivery.getAddress());

        DeliveryResponseDTO.RaffleDTO raffleDto = null;
        if (deliveryStatus == DeliveryStatus.SHIPPING_EXPIRED)
            raffleDto = toRaffleDto(delivery);

        String courierName = null;
        if (delivery.getCourier() != null)
            courierName = delivery.getCourier().getName();

        return toWinnerResultDto(delivery, addressDto, raffleDto, courierName);
    }

    @Override
    @Transactional
    public DeliveryResponseDTO.ResponseDto setAddress(Long deliveryId) {
        User user = getUser();
        Delivery delivery = getDeliveryById(deliveryId);
        validateWinner(delivery, user);

        if (delivery.getDeliveryStatus() != DeliveryStatus.WAITING_ADDRESS)
            throw new CustomException(ErrorStatus.DELIVERY_FAIL);

        List<Address> addressList = user.getAddresses();
        if (addressList.isEmpty())
            throw new CustomException(ErrorStatus.DELIVERY_NO_ADDRESS);

        Address defaultAddress = addressList.stream()
                .filter(Address::isDefault)
                .findFirst()
                .orElseThrow(() -> new CustomException(ErrorStatus.DELIVERY_NO_ADDRESS));

        schedulerService.scheduleAddressCheckJob(delivery);

        delivery.setAddress(defaultAddress);
        delivery.setDeliveryStatus(DeliveryStatus.READY);
        delivery.setShippingDeadline();

        schedulerService.scheduleInvoiceCheckJob(delivery);

        schedulerService.cancelDeliveryJob(delivery, "Address");
        schedulerService.scheduleDeliveryJob(delivery);

        emailService.sendOwnerReadyEmail(delivery);

        return toDeliveryResponseDto(deliveryId);
    }

    @Override
    @Transactional
    public DeliveryResponseDTO.ResponseDto success(Long deliveryId) {
        User user = getUser();
        Delivery delivery = getDeliveryById(deliveryId);
        validateWinner(delivery, user);

        if (delivery.getDeliveryStatus() != DeliveryStatus.SHIPPED)
            throw new CustomException(ErrorStatus.DELIVERY_FAIL);

        schedulerService.cancelDeliveryJob(delivery, "Complete");

        finalize(delivery);

        return toDeliveryResponseDto(deliveryId);
    }

    @Override
    @Transactional
    public DeliveryResponseDTO.WaitDto waitShipping(Long deliveryId) {
        User user = getUser();
        Delivery delivery = getDeliveryById(deliveryId);
        validateWinner(delivery, user);

        if (delivery.getDeliveryStatus() != DeliveryStatus.SHIPPING_EXPIRED)
            throw new CustomException(ErrorStatus.DELIVERY_FAIL);

        if (delivery.isShippingExtended())
            throw new CustomException(ErrorStatus.DELIVERY_ALREADY_EXTEND);

        schedulerService.cancelDeliveryJob(delivery, "Waiting");
        schedulerService.cancelDeliveryJob(delivery, "Shipping");
      
        delivery.extendShippingDeadline();

        schedulerService.scheduleDeliveryJob(delivery);

        return toWaitDto(delivery);
    }

    @Override
    @Transactional
    public DeliveryResponseDTO.ResponseDto cancel(Long deliveryId) {
        User user = getUser();
        Delivery delivery = getDeliveryById(deliveryId);
        validateWinner(delivery, user);

        if (delivery.getDeliveryStatus() != DeliveryStatus.SHIPPING_EXPIRED)
            throw new CustomException(ErrorStatus.DELIVERY_FAIL);

        schedulerService.cancelDeliveryJob(delivery, "Waiting");

        Long userId = baseController.getCurrentUserId();
        kakaoPayService.cancelPayment(userId);

        delivery.setDeliveryStatus(DeliveryStatus.CANCELLED);

        Raffle raffle = delivery.getRaffle();
        drawService.cancel(raffle);

        emailService.sendOwnerCancelEmail(raffle);

        return toDeliveryResponseDto(deliveryId);
    }

    @Override
    public DeliveryResponseDTO.OwnerResultDto getResult(Long deliveryId) {
        User user = getUser();
        Delivery delivery = getDeliveryById(deliveryId);
        validateOwner(delivery, user);

        Raffle raffle = delivery.getRaffle();
        int applyNum = raffle.getApplyList().size();
        int ticket = raffle.getTicketNum();

        DeliveryStatus deliveryStatus = delivery.getDeliveryStatus();
        if (deliveryStatus != DeliveryStatus.READY
                && deliveryStatus != DeliveryStatus.SHIPPED
                && deliveryStatus != DeliveryStatus.COMPLETED)
            return toOwnerResultDto(delivery, applyNum * ticket, null);

        MypageResponseDTO.AddressDto addressDto = toAddressDto(delivery.getAddress());
        return toOwnerResultDto(delivery, applyNum * ticket, addressDto);
    }

    @Override
    @Transactional
    public DeliveryResponseDTO.ResponseDto addInvoice(
            Long deliveryId, DeliveryRequestDTO deliveryRequestDTO) {

        User user = getUser();
        Delivery delivery = getDeliveryById(deliveryId);
        validateOwner(delivery, user);

        Courier courier = courierRepository.findByName(deliveryRequestDTO.getCourierName())
                .orElseThrow(() -> new CustomException(ErrorStatus.COMMON_WRONG_PARAMETER));

        if (delivery.getDeliveryStatus() != DeliveryStatus.READY)
            throw new CustomException(ErrorStatus.DELIVERY_FAIL);

        schedulerService.cancelDeliveryJob(delivery, "Shipping");

        delivery.setCourier(courier);
        delivery.setInvoiceNumber(deliveryRequestDTO.getInvoiceNumber());
        delivery.setDeliveryStatus(DeliveryStatus.SHIPPED);

        schedulerService.scheduleDeliveryJob(delivery);

        return toDeliveryResponseDto(deliveryId);
    }

    @Override
    @Transactional
    public DeliveryResponseDTO.ResponseDto editInvoice(
            Long deliveryId, DeliveryRequestDTO deliveryRequestDTO) {

        User user = getUser();
        Delivery delivery = getDeliveryById(deliveryId);
        validateOwner(delivery, user);

        Courier courier = courierRepository.findByName(deliveryRequestDTO.getCourierName())
                .orElseThrow(() -> new CustomException(ErrorStatus.COMMON_WRONG_PARAMETER));

        // 배송 상태가 SHIPPED 또는 READY일 경우만 운송장 정보 수정 가능
        if (delivery.getDeliveryStatus() != DeliveryStatus.READY &&
                delivery.getDeliveryStatus() != DeliveryStatus.SHIPPED) {
            throw new CustomException(ErrorStatus.DELIVERY_FAIL);
        }

        // 기존 배송 스케줄러 작업 취소
        schedulerService.cancelDeliveryJob(delivery, "Shipping");

        // 운송장 정보 및 택배사 정보 수정
        delivery.setCourier(courier);
        delivery.setInvoiceNumber(deliveryRequestDTO.getInvoiceNumber());

        // 배송 상태가 READY였을 경우에만 SHIPPED로 상태 변경
        if (delivery.getDeliveryStatus() == DeliveryStatus.READY) {
            delivery.setDeliveryStatus(DeliveryStatus.SHIPPED);
        }

        // 새로운 스케줄러 작업 등록
        schedulerService.scheduleDeliveryJob(delivery);

        return toDeliveryResponseDto(deliveryId);
    }

    @Override
    @Transactional
    public DeliveryResponseDTO.WaitDto waitAddress(Long deliveryId) {
        User user = getUser();
        Delivery delivery = getDeliveryById(deliveryId);
        validateOwner(delivery, user);

        if (delivery.getDeliveryStatus() != DeliveryStatus.ADDRESS_EXPIRED)
            throw new CustomException(ErrorStatus.DELIVERY_FAIL);

        if (delivery.isAddressExtended())
            throw new CustomException(ErrorStatus.DELIVERY_ALREADY_EXTEND);

        schedulerService.cancelDeliveryJob(delivery, "Waiting");
        schedulerService.cancelDeliveryJob(delivery, "Address");

        delivery.extendAddressDeadline();

        schedulerService.scheduleDeliveryJob(delivery);

        notificationService.sendWinnerForExtendedDeliveryDue(delivery);

        return toWaitDto(delivery);
    }

    private User getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new CustomException(ErrorStatus.USER_NOT_FOUND);
        }
        return userRepository.findById(Long.parseLong(authentication.getName()))
                .orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));
    }

    private Delivery getDeliveryById(Long deliveryId) {
        return deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new CustomException(ErrorStatus.DELIVERY_NOT_FOUND));
    }

    private void validateWinner(Delivery delivery, User user) {
        if (!user.equals(delivery.getWinner()))
            throw new CustomException(ErrorStatus.DELIVERY_NOT_WINNER);
    }

    private void validateOwner(Delivery delivery, User user) {
        if (!user.equals(delivery.getUser()))
            throw new CustomException(ErrorStatus.DELIVERY_NOT_OWNER);
    }

    @Override
    @Transactional
    public void finalize(Delivery delivery) {
        delivery.setDeliveryStatus(DeliveryStatus.COMPLETED);

        Raffle raffle = delivery.getRaffle();
        raffle.setRaffleStatus(RaffleStatus.COMPLETED);

        User user = raffle.getUser();
        int applyNum = raffle.getApplyList().size();
        int ticket = raffle.getTicketNum();

        user.setTicket_num(user.getTicket_num() + (ticket * applyNum));
    }
}
