package com.example.demo.service.general.impl;

import com.example.demo.base.code.exception.CustomException;
import com.example.demo.base.status.ErrorStatus;
import com.example.demo.domain.converter.RaffleConverter;
import com.example.demo.domain.dto.Raffle.RaffleRequestDTO;
import com.example.demo.domain.dto.Raffle.RaffleResponseDTO;
import com.example.demo.entity.*;
import com.example.demo.entity.base.enums.DeliveryStatus;
import com.example.demo.entity.base.enums.RaffleStatus;
import com.example.demo.repository.*;
import com.example.demo.service.general.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.demo.domain.converter.RaffleConverter.toApplyDto;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class RaffleServiceImpl implements RaffleService {

    private final RaffleRepository raffleRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final SchedulerService schedulerService;
    private final S3UploadService s3UploadService;
    private final ImageService imageService;
    private final ApplyRepository applyRepository;
    private final DeliveryRepository deliveryRepository;
    private final FollowRepository followRepository;
    private final LikeRepository likeRepository;

    @Override
    @Transactional
    public RaffleResponseDTO.ResponseDTO uploadRaffle(RaffleRequestDTO.UploadDTO request) {

        // 0. 작성자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new CustomException(ErrorStatus.USER_NOT_FOUND);
        }
        Long userId = Long.parseLong(authentication.getName());
        log.info("작성자 id {}", userId);

        User user = userRepository.findById(userId).orElseThrow();
//
//        // a. 사업자가 아닐 시 래플 업로드 불가능
//        if (!user.getIsBusiness()){
//            throw new CustomException(ErrorStatus.RAFFLE_ONLY_BUSINESS);
//        }

        // 1. 요청받은 카테고리 이름으로 Category 엔티티 가져오기
        Category category = categoryRepository.findByName(request.getCategory())
                .orElseThrow(() -> new CustomException(ErrorStatus.CATEGORY_NOT_FOUND));

        // 2. S3에 파일 업로드
        List<String> imageUrls = s3UploadService.saveFile(request.getFiles());

        // 3. 래플 생성
        int minUser = (request.getMinTicket() + request.getTicketNum() - 1)/ request.getTicketNum();
        Raffle raffle = RaffleConverter.toRaffle(request, category ,user, minUser);

        // 4. 이미지 엔티티 생성
        List<Image> images = imageService.saveImages(imageUrls);

        // 5. 래플과 이미지 엔티티 매핑
        images.forEach(img -> raffle.addImage(img));

        // 6. 래플 저장
        raffleRepository.save(raffle);

        schedulerService.scheduleRaffleJob(raffle);

        // 7. 래플 엔티티를 ResponseDTO로 변환 후 반환
        return RaffleConverter.toRaffleResponseDTO(raffle);
    }

    @Override
    @Transactional
    public Long softDeleteRaffle(Long id) {
        Raffle raffle = raffleRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorStatus.RAFFLE_NOT_FOUND));

        RaffleStatus status = raffle.getRaffleStatus();

        switch (status) {
            case UNOPENED, CANCELLED, COMPLETED:
                softDelete(raffle);
                break;
            case ACTIVE:
                List<Apply> applyList = raffle.getApplyList();
                if(applyList != null && !applyList.isEmpty()){
                    // TODO 패널티 부과 로직 추가

                    int refundTicket = raffle.getTicketNum();
                    List<Long> userIdList = applyList.stream()
                                    .map(apply -> apply.getUser().getId())
                                    .collect(Collectors.toList());

                    if (!userIdList.isEmpty() && refundTicket > 0) {
                        userRepository.batchUpdateTicketNum(refundTicket, userIdList);
                    }

                }
                softDelete(raffle);
                break;
            case UNFULFILLED, ENDED:
                throw new CustomException(ErrorStatus.RAFFLE_CANT_DELETE);
            case DELETED:
                throw new CustomException(ErrorStatus.RAFFLE_ALREADY_DELETED);
        }
        return id;
    }

    private void softDelete(Raffle raffle) {
        schedulerService.cancelRaffleJob(raffle);
        raffle.setRaffleStatus(RaffleStatus.DELETED);
        raffle.setDeletedAt(LocalDateTime.now());
    }

    @Override
    @Transactional
    public RaffleResponseDTO.RaffleDetailDTO getRaffleDetailsDTO(Long raffleId) {

        // 요청받은 래플 id로 엔티티 조회
        Raffle raffle = raffleRepository.findById(raffleId)
                .orElseThrow(() -> new CustomException(ErrorStatus.RAFFLE_NOT_FOUND));

        // userId 조회
        Long raffleUserId = raffle.getUser().getId();

        // 필요 데이터 조회 (쿼리 4개 날아가서 추후 개선 예정)
        int likeCount, applyCount, followCount, reviewCount;
        String state;
        String isWinner = "no";
        Long deliveryId = null;
        boolean followStatus = false;
        boolean likeStatus=false;

        likeCount = raffleRepository.countLikeByRaffleId(raffleId);
        applyCount = raffleRepository.countApplyByRaffleId(raffleId);
        reviewCount = raffleRepository.countReviewsByUserId(raffleUserId);

        followCount = Boolean.TRUE.equals(raffle.getUser().getFollowerVisible()) ? raffleRepository.countFollowsByUserId(raffleUserId) : -2;

        // 유저 정보 받아오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 비회원인 경우
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())){
            log.info("비회원 접근");
            state = "nonParticipant";
        }

        // 회원인 경우
        else {
            log.info("회원 접근");
            Long currentUserid = Long.parseLong(authentication.getName());
            User user = userRepository.findById(currentUserid)
                    .orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));

            // 사용자가 개최자인 경우
            if (currentUserid.equals(raffleUserId)) {
                state = "host";

                Delivery delivery = deliveryRepository.findByRaffleAndDeliveryStatusIn(raffle, List.of(
                        DeliveryStatus.WAITING_ADDRESS, DeliveryStatus.READY,
                        DeliveryStatus.SHIPPED, DeliveryStatus.COMPLETED));

                if (delivery != null)
                    deliveryId = delivery.getId();
            }
            // 사용자가 이미 응모한 경우
            else if (applyRepository.existsByRaffleAndUser(raffle, user)) {
                state = "participant";
            }
            // 사용자가 응모 가능한 경우
            else {
                state = "nonParticipant";
            }

            User winner = raffle.getWinner();
            if (winner == null) {
                isWinner = "hope";
            } else{

                boolean isChecked = false;
                Apply apply = applyRepository.findByRaffleAndUser(raffle, user);
                if (apply != null)
                    isChecked = apply.isChecked();

                if (winner == user) {
                    isWinner = "yes";
                } else if (!isChecked) {
                    isWinner = "hope";
                } else {
                    isWinner = "no";
                }
            }

            followStatus = followRepository.existsByUserIdAndStoreId(currentUserid, raffleUserId);
            likeStatus = likeRepository.existsByRaffleAndUser(raffle,user);
        }

        RaffleStatus raffleStatus = raffle.getRaffleStatus();


        // 3. 조회 수 증가
        raffle.addView();

        // 4. DTO 변환 및 반환
        return RaffleConverter.toDetailDTO(raffle, likeCount, applyCount, followCount, reviewCount, state, isWinner, raffleStatus, deliveryId, followStatus,likeStatus);
    }

    @Override
    @Transactional
    public RaffleResponseDTO.ApplyResultDTO apply(Long raffleId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new CustomException(ErrorStatus.USER_NOT_FOUND);
        }
        User user = userRepository.findById(Long.parseLong(authentication.getName()))
                .orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));

        Raffle raffle = raffleRepository.findById(raffleId)
                .orElseThrow(() -> new CustomException(ErrorStatus.RAFFLE_NOT_FOUND));

        int userTicket = user.getTicket_num();
        int raffleTicket = raffle.getTicketNum();

        if (raffle.getRaffleStatus() != RaffleStatus.ACTIVE)
            throw new CustomException(ErrorStatus.APPLY_RAFFLE_UNAVAILABLE);

        if (raffle.getUser().equals(user))
            throw new CustomException(ErrorStatus.APPLY_SELF_RAFFLE);

        if (applyRepository.existsByRaffleAndUser(raffle, user))
            throw new CustomException(ErrorStatus.APPLY_ALREADY_APPLIED);

        if (userTicket < raffleTicket) {
            RaffleResponseDTO.FailedApplyDTO failedApplyDTO = RaffleResponseDTO.FailedApplyDTO.builder()
                    .missingTickets(raffleTicket - userTicket)
                    .build();

            return RaffleResponseDTO.ApplyResultDTO.builder()
                    .applyDTO(null)
                    .failedApplyDTO(failedApplyDTO)
                    .build();
        }

        user.setTicket_num(userTicket - raffleTicket);

        Apply apply = Apply.builder()
                .raffle(raffle)
                .user(user)
                .build();
        raffle.addApply(apply);

        return RaffleResponseDTO.ApplyResultDTO.builder()
                .applyDTO(toApplyDto(apply))
                .failedApplyDTO(null)
                .build();
    }
}
