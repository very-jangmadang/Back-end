package com.example.demo.service.general.impl;
import com.example.demo.base.code.exception.CustomException;
import com.example.demo.base.status.ErrorStatus;
import com.example.demo.domain.converter.NotificationConverter;
import com.example.demo.domain.converter.RaffleConverter;
import com.example.demo.domain.dto.Notification.NotificationRequestDTO;
import com.example.demo.domain.dto.Notification.NotificationResponseDTO;
import com.example.demo.entity.*;
import com.example.demo.entity.base.enums.Notification.NotificationEvent;
import com.example.demo.entity.base.enums.Notification.NotificationType;
import com.example.demo.repository.NotificationRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.general.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final NotificationConverter notificationConverter;

    // 개최자용 알림
    @Transactional
    public void sendHostForEndedRaffle(Raffle raffle) {
        User host = raffle.getUser(); // 개최자

        NotificationRequestDTO.ForHost request = new NotificationRequestDTO.ForHost();
        request.setUser(host);
        request.setEvent(NotificationEvent.RAFFLE_ENDED);
        request.setTitle("[" + raffle.getName() + "] 래플이 마감되었습니다");
        request.setMessage("당첨자를 확인하고 배송지 입력을 요청해 주세요.");
        request.setAction("/raffles/" + raffle.getId() + "/result");

        Notification notification = NotificationConverter.toNotification(request, host);
        notificationRepository.save(notification);
    }

    @Transactional
    public void sendHostForUnenteredAddress(Delivery delivery){
        Raffle raffle = delivery.getRaffle();
        User host = raffle.getUser(); // 개최자

        NotificationRequestDTO.ForHost request = new NotificationRequestDTO.ForHost();
        request.setUser(host);
        request.setEvent(NotificationEvent.DELIVERY_ADDRESS_MISSING);
        request.setTitle("당첨자의 배송지 입력이 지연 중입니다.");
        request.setMessage("재추첨을 진행할지, 당첨자의 응답을 추가로 기다릴지 선택해주세요.");
        request.setAction("/delivery/" + delivery.getId() + "/owner/wait");

        Notification notification = NotificationConverter.toNotification(request, host);
        notificationRepository.save(notification);
    }

    @Transactional
    public void sendHostForUnenteredInvoice(Delivery delivery){
        Raffle raffle = delivery.getRaffle();
        User host = raffle.getUser(); // 개최자

        NotificationRequestDTO.ForHost request = new NotificationRequestDTO.ForHost();
        request.setUser(host);
        request.setEvent(NotificationEvent.DELIVERY_INVOICE_MISSING);
        request.setTitle("["+raffle.getName()+"상품] 운송장 등록 마감 1시간 전입니다.");
        request.setMessage("아직 송장이 입력되지 않았습니다. 등록해 주세요.");
        request.setAction("/delivery/" + delivery.getId() + "/owner");

        Notification notification = NotificationConverter.toNotification(request, host);
        notificationRepository.save(notification);
    }


    @Override
    public List<NotificationResponseDTO> getHostNotifications() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new CustomException(ErrorStatus.USER_NOT_FOUND);
        }
        Long userId = Long.parseLong(authentication.getName());

        User user = userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));


        LocalDateTime twoWeeksAgo = LocalDateTime.now().minusWeeks(2);
        List<Notification> notifications = notificationRepository.findByUserAndEventInAndCreatedAtAfter(user,HOST_EVENTS, twoWeeksAgo);

        return notifications.stream()
                .map(notificationConverter::toResponseDTO)
                .collect(Collectors.toList());
    }

    // 당첨자용 알림
    @Override
    public List<NotificationResponseDTO> getWinnerNotifications() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new CustomException(ErrorStatus.USER_NOT_FOUND);
        }
        Long userId = Long.parseLong(authentication.getName());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));

        LocalDateTime twoWeeksAgo = LocalDateTime.now().minusWeeks(2);
        List<Notification> notifications = notificationRepository.findByUserAndEventInAndCreatedAtAfter(user,WINNER_EVENTS, twoWeeksAgo);

        return notifications.stream()
                .map(notificationConverter::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void sendWinnerForEndedRaffle(Raffle raffle) {
        User winner = raffle.getWinner(); // 개최자

        NotificationRequestDTO.ForHost request = new NotificationRequestDTO.ForHost();
        request.setUser(winner);
        request.setEvent(NotificationEvent.RAFFLE_RESULT);
        request.setTitle("축하합니다!! [" + raffle.getName() + "] 래플에 당첨되셨습니다.");
        request.setMessage("배송지 입력 후 결제를 완료해 주세요.");
        request.setAction("/raffles/" + raffle.getId() + "/draw");

        Notification notification = NotificationConverter.toNotification(request, winner);
        notificationRepository.save(notification);
    }










    private static final List<NotificationEvent> HOST_EVENTS = List.of(
            NotificationEvent.RAFFLE_ENDED,
            NotificationEvent.DELIVERY_ADDRESS_MISSING,
            NotificationEvent.DELIVERY_INVOICE_MISSING
    );

    private static final List<NotificationEvent> WINNER_EVENTS = List.of(
            NotificationEvent.RAFFLE_RESULT,
            NotificationEvent.DELIVERY_ADDRESS_REQUIRED,
            NotificationEvent.DELIVERY_DELAYED,
            NotificationEvent.REVIEW_REQUEST,
            NotificationEvent.DELIVERY_ADDRESS_DUE
    );

}
