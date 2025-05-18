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
import com.example.demo.repository.*;
import com.example.demo.service.general.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
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
    private final LikeRepository likeRepository;
    private final NotificationConverter notificationConverter;
    private final ApplyRepository applyRepository;

    // 공통 알림 생성 메서드
    private void sendNotification(User user, NotificationEvent event, String title, String message, String action) {
        NotificationRequestDTO request = new NotificationRequestDTO();
        request.setUser(user);
        request.setEvent(event);
        request.setTitle(title);
        request.setMessage(message);
        request.setAction(action);

        Notification notification = NotificationConverter.toNotification(request);
        notificationRepository.save(notification);
    }

    // 개최자용 알림
    @Transactional
    public void sendHostForEndedRaffle(Raffle raffle) {
        User host = raffle.getUser(); // 개최자
        String title = "[" + raffle.getName() + "] 래플이 마감되었습니다";
        String message = "당첨자를 확인하고 배송지 입력을 요청해 주세요.";
        String action = "/raffles/" + raffle.getId() + "/result";

        sendNotification(host, NotificationEvent.RAFFLE_ENDED, title, message, action);
    }

    @Transactional
    public void sendHostForUnenteredAddress(Delivery delivery) {
        Raffle raffle = delivery.getRaffle();
        User host = raffle.getUser(); // 개최자
        String title = "당첨자의 배송지 입력이 지연 중입니다.";
        String message = "재추첨을 진행할지, 당첨자의 응답을 추가로 기다릴지 선택해주세요.";
        String action = "/delivery/" + delivery.getId() + "/owner/wait";

        sendNotification(host, NotificationEvent.DELIVERY_ADDRESS_MISSING, title, message, action);
    }

    @Transactional
    public void sendHostForUnenteredInvoice(Delivery delivery) {
        Raffle raffle = delivery.getRaffle();
        User host = raffle.getUser(); // 개최자
        String title = "[" + raffle.getName() + "상품] 운송장 등록 마감 1시간 전입니다.";
        String message = "아직 송장이 입력되지 않았습니다. 등록해 주세요.";
        String action = "/delivery/" + delivery.getId() + "/owner";

        sendNotification(host, NotificationEvent.DELIVERY_INVOICE_MISSING, title, message, action);
    }

    // 당첨자용 알림
    @Transactional
    public void sendWinnerForUnenteredAddress(Delivery delivery) {
        Raffle raffle = delivery.getRaffle();
        User winner = raffle.getWinner(); // 당첨자
        String title = "[" + raffle.getName() + "] 상품 배송지 등록 마감 1시간 전입니다.";
        String message = "1시간 내에 입력하지 않으면 취소될 수 있습니다.";
        String action = "/delivery/" + delivery.getId() + "/winner";

        sendNotification(winner, NotificationEvent.DELIVERY_ADDRESS_CHECK, title, message, action);
    }

    @Transactional
    public void sendWinnerForUnenteredInvoice(Delivery delivery) {
        Raffle raffle = delivery.getRaffle();
        User winner = raffle.getWinner(); // 당첨자

        // 중복 알림 체크
        boolean exists = notificationRepository.existsByDelivery_IdAndEvent(delivery.getId(), NotificationEvent.INVOICE_DUE_OVER);
        if (exists) {
            return; // 이미 존재하는 알림은 보내지 않음
        }

        String title = "[" + raffle.getName() + "상품] . 판매자가 송장을 아직 등록하지 않았어요.";
        String message = "당첨자가 조치를 선택할 수 있습니다.";
        String action = "/delivery/" + delivery.getId() + "/winner";

        sendNotification(winner, NotificationEvent.INVOICE_DUE_OVER, title, message, action);
    }

    @Transactional
    public void sendWinnerForEndedRaffle(User user,Raffle raffle) {
        User winner = raffle.getWinner(); // 당첨자
        String title = "축하합니다!! [" + raffle.getName() + "] 래플에 당첨되셨습니다.";
        String message = "배송지 입력 후 결제를 완료해 주세요.";
        String action = "/raffles/" + raffle.getId() + "/draw";

        sendNotification(user, NotificationEvent.RAFFLE_RESULT, title, message, action);
    }

    @Transactional
    public void sendWinnerForExtendedDeliveryDue(Delivery delivery) {
        Raffle raffle = delivery.getRaffle();
        User winner = raffle.getWinner(); // 당첨자
        String title = "[" + raffle.getName() + "] 판매자가 배송지 입력기한을 연장하였습니다.";
        String message = "24시간 내에 배송지 입력 후 결제를 완료해 주세요.";
        String action = "/delivery/" + delivery.getId() + "/winner";

        sendNotification(winner, NotificationEvent.DELIVERY_DUE_EXTENDED, title, message, action);
    }

    @Transactional
    public void sendWinnerForCancel(Raffle raffle) {
        User winner = raffle.getWinner(); // 당첨자
        String title = "[" + raffle.getName() + "] 판매자가 래플 당첨을 취소하였습니다.";
        String message = "배송지 미입력으로 래플 당첨이 취소되었습니다.";
        String action = "/raffle/" + raffle.getId() + "/winner";

        sendNotification(winner, NotificationEvent.DELIVERY_DUE_CANCELLED, title, message, action);
    }

    //찜한 사람에게 오픈 알림
    @Transactional
    public void sendAllForOpen(Raffle raffle) {
        List<Like> likes = likeRepository.findByRaffleWithUser(raffle);
        List<User> users = likes.stream()
                .map(Like::getUser)
                .collect(Collectors.toList());

        String title = "[" + raffle.getName() + "] 찜한 래플이 시작되었습니다!";
        String message = "지금 바로 참여해보세요!";
        String action = "/raffle/" + raffle.getId()+"/apply";

        for (User user : users) {
            sendNotification(user, NotificationEvent.RAFFLE_OPENED, title, message, action);
        }
    }

    @Transactional
    public void sendApplicantForEnd(Raffle raffle) {
        List<User> applicants = applyRepository.findUsersByRaffle(raffle);
        String title = "[" + raffle.getName() + "] 응모한 래플이 종료되었습니다!";
        String message = "지금 바로 결과를 확인하세요!";
        String action = "/raffle/" + raffle.getId()+"/draw";

        for (User user : applicants) {
            sendNotification(user, NotificationEvent.RAFFLE_FINISHED, title, message, action);
        }
    }

    @Override
    public List<NotificationResponseDTO> getUserNotifications() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new CustomException(ErrorStatus.USER_NOT_FOUND);
        }
        Long userId = Long.parseLong(authentication.getName());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));

        LocalDateTime twoWeeksAgo = LocalDateTime.now().minusWeeks(2);
        List<Notification> notifications = notificationRepository.findByUserAndEventInAndCreatedAtAfter(user, ALL_EVENTS, twoWeeksAgo);

        return notifications.stream()
                .map(notificationConverter::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<NotificationResponseDTO> getApplicantNotifications() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new CustomException(ErrorStatus.USER_NOT_FOUND);
        }
        Long userId = Long.parseLong(authentication.getName());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));

        LocalDateTime twoWeeksAgo = LocalDateTime.now().minusWeeks(2);
        List<Notification> notifications = notificationRepository.findByUserAndEventInAndCreatedAtAfter(user, APPLICANT_EVENTS, twoWeeksAgo);

        return notifications.stream()
                .map(notificationConverter::toResponseDTO)
                .collect(Collectors.toList());
    }



    @Override
    public List<NotificationResponseDTO> getHostNotifications() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new CustomException(ErrorStatus.USER_NOT_FOUND);
        }
        Long userId = Long.parseLong(authentication.getName());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));

        LocalDateTime twoWeeksAgo = LocalDateTime.now().minusWeeks(2);
        List<Notification> notifications = notificationRepository.findByUserAndEventInAndCreatedAtAfter(user, HOST_EVENTS, twoWeeksAgo);

        return notifications.stream()
                .map(notificationConverter::toResponseDTO)
                .collect(Collectors.toList());
    }

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
        List<Notification> notifications = notificationRepository.findByUserAndEventInAndCreatedAtAfter(user, WINNER_EVENTS, twoWeeksAgo);

        return notifications.stream()
                .map(notificationConverter::toResponseDTO)
                .collect(Collectors.toList());
    }



    private static final List<NotificationEvent> ALL_EVENTS = List.of(
            NotificationEvent.RAFFLE_OPENED
    );

    private static final List<NotificationEvent> APPLICANT_EVENTS = List.of(
            NotificationEvent.RAFFLE_FINISHED
    );

    private static final List<NotificationEvent> HOST_EVENTS = List.of(
            NotificationEvent.RAFFLE_ENDED,
            NotificationEvent.DELIVERY_ADDRESS_MISSING,
            NotificationEvent.DELIVERY_INVOICE_MISSING
    );

    private static final List<NotificationEvent> WINNER_EVENTS = List.of(
            NotificationEvent.RAFFLE_RESULT,
            NotificationEvent.DELIVERY_ADDRESS_REQUIRED,
            NotificationEvent.DELIVERY_ADDRESS_CHECK,
            NotificationEvent.DELIVERY_DELAYED,
            NotificationEvent.REVIEW_REQUEST,
            NotificationEvent.DELIVERY_ADDRESS_DUE,
            NotificationEvent.INVOICE_DUE_OVER,
            NotificationEvent.DELIVERY_DUE_EXTENDED,
            NotificationEvent.DELIVERY_DUE_CANCELLED
    );

}
