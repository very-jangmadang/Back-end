package com.example.demo.service.general.impl;

import com.example.demo.base.Constants;
import com.example.demo.base.code.exception.CustomException;
import com.example.demo.base.status.ErrorStatus;
import com.example.demo.domain.converter.MypageConverter;
import com.example.demo.domain.converter.ReviewConverter;
import com.example.demo.domain.dto.Mypage.MypageRequestDTO;
import com.example.demo.domain.dto.Mypage.MypageResponseDTO;
import com.example.demo.entity.*;
import com.example.demo.entity.base.enums.DeliveryStatus;
import com.example.demo.domain.dto.Review.ReviewResponseDTO;
import com.example.demo.domain.dto.Review.ReviewWithAverageDTO;
import com.example.demo.repository.*;
import com.example.demo.service.general.MypageService;
import com.example.demo.service.general.S3UploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.example.demo.domain.converter.MypageConverter.toAddress;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MypageServiceImpl implements MypageService {

    private final UserRepository userRepository;
    private final ApplyRepository applyRepository;
    private final LikeRepository likeRepository;
    private final ReviewRepository reviewRepository;
    private final S3UploadService s3UploadService;
    private final AddressRepository addressRepository;
    private final DeliveryRepository deliveryRepository;
    private final RaffleRepository raffleRepository;


    @Transactional
    // 프로필 이미지 업데이트
    public String updateProfileImage(Long userId, MultipartFile profile) {

        // 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));

        // 이미지 업로드 후 URL 얻기
        String imageUrl = s3UploadService.saveSingleFile(profile);

        // 사용자 프로필 이미지 URL 업데이트
        user.setProfileImageUrl(imageUrl);

        return imageUrl;
    }

    @Transactional
    // 닉네임 변경
    public String changeNickname(Long userId, String nickname) {

        // 닉네임 유효성 검사
        if (!nickname.matches("^[a-zA-Z0-9가-힣]{2,10}$")) {
            throw new CustomException(ErrorStatus.INVALID_NICKNAME);
        }

        // 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));

        if (userRepository.existsByNickname(nickname)) {
            throw new CustomException(ErrorStatus.NICKNAME_ALREADY_EXISTS);
        }

        // 닉네임 업데이트
        user.setNickname(nickname);

        return nickname;
    }


    //내 리뷰 조회
    public ReviewWithAverageDTO getMyReviewsByUserId(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));

        // 사용자의 모든 후기 조회
        List<Review> reviews = reviewRepository.findAllByUser(user);

        List<ReviewResponseDTO> reviewResponseDTO = ReviewConverter.toReviewResponseDTOList(reviews);

        int reviewCount = reviews.size();

        double averageScore = user.getAverageScore();

        return new ReviewWithAverageDTO(reviewResponseDTO, averageScore, reviewCount);
    }

    @Transactional
    public boolean updateFollowerVisibility(Long userId, boolean isVisible) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));

        // 팔로워 수 공개 여부 설정
        user.setFollowerVisible(isVisible);
        userRepository.updateFollowerVisibility(userId, isVisible);

        return isVisible;
    }

    public boolean getFollowerVisibility(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));

        return user.getFollowerVisible();
    }
  
    @Override
    public MypageResponseDTO.AddressListDto getAddresses() {

        User user = getUser();
        List<Address> addressList = user.getAddresses();

        List<MypageResponseDTO.AddressDto> addressDtos = addressList.stream()
                .map(MypageConverter::toAddressDto)
                .toList();

        return MypageResponseDTO.AddressListDto.builder()
                .addressList(addressDtos)
                .build();
    }

    @Override
    @Transactional
    public MypageResponseDTO.AddressListDto setDefault(MypageRequestDTO.AddressDto addressDto) {

        User user = getUser();

        Address address = addressRepository.findById(addressDto.getAddressId())
                .orElseThrow(() -> new CustomException(ErrorStatus.ADDRESS_NOT_FOUND));

        if (!address.getUser().equals(user))
            throw new CustomException(ErrorStatus.ADDRESS_MISMATCH_USER);

        List<Address> addressList = user.getAddresses();

        if (!address.isDefault())
            address.setDefaultAddress();

        List<MypageResponseDTO.AddressDto> addressDtos = addressList.stream()
                .map(MypageConverter::toAddressDto)
                .toList();

        return MypageResponseDTO.AddressListDto.builder()
                .addressList(addressDtos)
                .build();
    }

    @Override
    @Transactional
    public void addAddress(MypageRequestDTO.AddressAddDto addressAddDto) {

        User user = getUser();

        if (user.getAddresses().size() == Constants.MAX_ADDRESS_COUNT)
            throw new CustomException(ErrorStatus.ADDRESS_FULL);

        if (addressAddDto.getMessage() != null && addressAddDto.getMessage().length() > 255)
            throw new CustomException(ErrorStatus.ADDRESS_LONG_MESSAGE);

        Address address = toAddress(addressAddDto);
        user.addAddress(address);

        if (address.isDefault() || user.getAddresses().size() == 1)
            address.setDefaultAddress();
    }

    @Override
    @Transactional
    public void deleteAddress(MypageRequestDTO.AddressDeleteDto addressDeleteDto) {
        User user = getUser();

        if (user.getAddresses().size() == addressDeleteDto.getAddressIdList().size())
            throw new CustomException(ErrorStatus.ADDRESS_DEFAULT_LOCKED);

        List<Address> addressesList = addressRepository.findAllById(addressDeleteDto.getAddressIdList());
        if (addressesList.size() != addressDeleteDto.getAddressIdList().size() || addressesList.isEmpty())
            throw new CustomException(ErrorStatus.ADDRESS_NOT_FOUND);

        for (Address address : addressesList) {
            if (!address.getUser().equals(user))
                throw new CustomException(ErrorStatus.ADDRESS_MISMATCH_USER);

            if (address.isDefault() || user.getAddresses().size() == 1)
                throw new CustomException(ErrorStatus.ADDRESS_DEFAULT_LOCKED);

            boolean hasActiveDelivery = deliveryRepository.existsByAddressAndDeliveryStatusIn(
                    address, List.of(DeliveryStatus.READY, DeliveryStatus.SHIPPING_EXPIRED));

            if (hasActiveDelivery) {
                throw new CustomException(ErrorStatus.ADDRESS_HAS_ACTIVE_DELIVERY);
            }
        }

        user.getAddresses().removeAll(addressesList);
        addressRepository.deleteAll(addressesList);
    }

    @Override
    public MypageResponseDTO.MyPageInfoDto getMyPageMyApplyRaffles(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));
        List<Review> myReviews = reviewRepository.findAllByUser(user);

        List<Apply> applyList = applyRepository.findWithRaffleByUser(user);
        applyList.sort(Comparator.comparing(Apply::getCreatedAt, Comparator.reverseOrder()));

        List<Long> raffleIds = applyList.stream()
                .map(apply -> apply.getRaffle().getId())
                .collect(Collectors.toList());

        List<Object[]> applyCounts = applyRepository.countAppliesByRaffleIds(raffleIds);
        List<Object[]> likeStatuses = likeRepository.checkLikesByRaffleIdsAndUser(raffleIds, user);

        Map<Long, Integer> raffleApplyCountMap = applyCounts.stream()
                .collect(Collectors.toMap(result -> (Long) result[0], result -> ((Long) result[1]).intValue()));

        Map<Long, Boolean> raffleLikeMap = likeStatuses.stream()
                .collect(Collectors.toMap(result -> (Long) result[0], result -> (Boolean) result[1]));

        List<MypageResponseDTO.RaffleDto> applyListDtos = applyList.stream()
                .map(apply -> {
                    Raffle raffle = apply.getRaffle();
                    int applyNum = raffleApplyCountMap.getOrDefault(raffle.getId(), 0);
                    boolean isLiked = raffleLikeMap.getOrDefault(raffle.getId(), false);

                    return MypageConverter.toRaffleDto(raffle, applyNum, isLiked);
                })
                .collect(Collectors.toList());

        return MypageResponseDTO.MyPageInfoDto.builder()
                .nickname(user.getNickname())
                .reviewNum(myReviews.size())
                .followerNum(user.getFollowers().size())
                .profileImageUrl(user.getProfileImageUrl())
                .raffles(applyListDtos)
                .build();
    }

    @Override
    public MypageResponseDTO.MyPageInfoDto getMyPageMyHostRaffles(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));
        List<Review> myReviews = reviewRepository.findAllByUser(user);
        List<Raffle> myHostRaffles = raffleRepository.findAllByUserIdOrderByCreatedAtDesc(user.getId());

        List<MypageResponseDTO.RaffleDto> raffleDtoList = new ArrayList<>();

        for (Raffle myHostRaffle : myHostRaffles) {
            boolean likeStatus = likeRepository.findByUserIdAndRaffleId(userId, myHostRaffle.getId()).isPresent();
            MypageResponseDTO.RaffleDto raffleDto = MypageConverter.toRaffleDto(myHostRaffle, myHostRaffle.getApplyList().size(), likeStatus);
            raffleDtoList.add(raffleDto);
        }

            return MypageResponseDTO.MyPageInfoDto
                .builder()
                .nickname(user.getNickname())
                .followerNum(user.getFollowers().size())
                .reviewNum(myReviews.size())
                .profileImageUrl(user.getProfileImageUrl())
                .raffles(raffleDtoList)
                .build();
    }


    @Override
    public MypageResponseDTO.ProfileInfoDto getProfileHostRaffles(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));
        List<Review> myReviews = reviewRepository.findAllByUser(user);
        List<Raffle> myHostRaffles = raffleRepository.findAllByUserIdOrderByCreatedAtDesc(user.getId());

        List<MypageResponseDTO.RaffleDto> raffleDtoList = new ArrayList<>();

        for (Raffle myHostRaffle : myHostRaffles) {
            boolean likeStatus = likeRepository.findByUserIdAndRaffleId(userId, myHostRaffle.getId()).isPresent();
            MypageResponseDTO.RaffleDto raffleDto = MypageConverter.toRaffleDto(myHostRaffle, myHostRaffle.getApplyList().size(), likeStatus);
            raffleDtoList.add(raffleDto);
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean followStatus = false;

        // 회원인 경우 ( 팔로우 여부 전달을 위해 확인 )
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal())) {
            Long loginId = Long.parseLong(authentication.getName());
            followStatus = user.getFollowers().stream()
                    .anyMatch(follow -> follow.getUser() != null && follow.getUser().getId().equals(loginId));
        }

        Integer followerNum = Boolean.TRUE.equals(user.getFollowerVisible()) ? user.getFollowers().size() : -2;

        return MypageResponseDTO.ProfileInfoDto
                .builder()
                .nickname(user.getNickname())
                .followerNum(followerNum)
                .reviewNum(myReviews.size())
                .followStatus(followStatus)
                .profileImageUrl(user.getProfileImageUrl())
                .raffles(raffleDtoList)
                .build();
    }

    @Override
    public MypageResponseDTO.ProfileInfoWithReviewsDto getProfileReviews(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));
        List<Review> reviews = reviewRepository.findAllByUser(user);

        List<ReviewResponseDTO> reviewResponseDTO = ReviewConverter.toReviewResponseDTOList(reviews);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean followStatus = false;

        // 회원인 경우 ( 팔로우 여부 전달을 위해 확인 )
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal())) {
            Long loginId = Long.parseLong(authentication.getName());
            followStatus = user.getFollowers().stream()
                    .anyMatch(follow -> follow.getUser() != null && follow.getUser().getId().equals(loginId));
        }

        Integer followerNum = Boolean.TRUE.equals(user.getFollowerVisible()) ? user.getFollowers().size() : -2;

        return MypageResponseDTO.ProfileInfoWithReviewsDto.builder()
                .reviews(reviewResponseDTO)
                .reviewNum(reviews.size())
                .nickname(user.getNickname())
                .profileImageUrl(user.getProfileImageUrl())
                .followStatus(followStatus)
                .avgScore(user.getAverageScore())
                .followerNum(followerNum)
                .build();
    }

    @Override
    public MypageResponseDTO.CheckBusinessDto checkBusiness(){
        User user = getUser();

        return MypageResponseDTO.CheckBusinessDto
                .builder()
                .isBusiness(user.getIsBusiness())
                .nickname(user.getNickname())
                .build();
    }

    private User getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new CustomException(ErrorStatus.USER_NOT_FOUND);
        }
        return userRepository.findById(Long.parseLong(authentication.getName()))
                .orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));
    }


}
