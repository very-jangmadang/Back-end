package com.example.demo.service.general.impl;

import com.example.demo.base.Constants;
import com.example.demo.base.code.exception.CustomException;
import com.example.demo.base.status.ErrorStatus;
import com.example.demo.domain.converter.MypageConverter;
import com.example.demo.domain.dto.Mypage.MypageRequestDTO;
import com.example.demo.domain.dto.Mypage.MypageResponseDTO;
import com.example.demo.entity.*;
import com.example.demo.entity.base.enums.DeliveryStatus;
import com.example.demo.repository.*;
import com.example.demo.service.general.MypageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final AddressRepository addressRepository;
    private final DeliveryRepository deliveryRepository;

    @Override
    public MypageResponseDTO.ApplyListDto getApplies() {

        // 사용자 정보 가져오기 (JWT 기반 인증 후 추후 구현 예정)
        User user = userRepository.findById(2L)
                .orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));

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

        return MypageResponseDTO.ApplyListDto.builder()
                .raffleList(applyListDtos)
                .build();

    }

    @Override
    public MypageResponseDTO.AddressListDto getAddresses(Authentication authentication) {

        User user = getUser(authentication);
        List<Address> addressList = user.getAddresses();

        if (addressList == null || addressList.isEmpty())
            throw new CustomException(ErrorStatus.ADDRESS_EMPTY);

        List<MypageResponseDTO.AddressDto> addressDtos = addressList.stream()
                .map(MypageConverter::toAddressDto)
                .toList();

        return MypageResponseDTO.AddressListDto.builder()
                .addressList(addressDtos)
                .build();
    }

    @Override
    @Transactional
    public MypageResponseDTO.AddressListDto setDefault(
            MypageRequestDTO.AddressDto addressDto, Authentication authentication) {

        User user = getUser(authentication);

        Address address = addressRepository.findById(addressDto.getAddressId())
                .orElseThrow(() -> new CustomException(ErrorStatus.ADDRESS_NOT_FOUND));

        if (!address.getUser().equals(user))
            throw new CustomException(ErrorStatus.ADDRESS_MISMATCH_USER);

        List<Address> addressList = user.getAddresses();

        if (!address.isDefault()) {
            address.setDefaultAddress();
            addressRepository.save(address);
        }

        List<MypageResponseDTO.AddressDto> addressDtos = addressList.stream()
                .map(MypageConverter::toAddressDto)
                .toList();

        return MypageResponseDTO.AddressListDto.builder()
                .addressList(addressDtos)
                .build();
    }

    @Override
    @Transactional
    public void addAddress(MypageRequestDTO.AddressAddDto addressAddDto, Authentication authentication) {

        User user = getUser(authentication);

        if (user.getAddresses().size() == Constants.MAX_ADDRESS_COUNT)
            throw new CustomException(ErrorStatus.ADDRESS_FULL);

        Address address = toAddress(addressAddDto);
        user.addAddress(address);

        if (address.isDefault() || user.getAddresses().size() == 1)
            address.setDefaultAddress();

        String message = addressAddDto.getMessage();
        if (message != null) {
            if (message.length() > 255)
                throw new CustomException(ErrorStatus.ADDRESS_LONG_MESSAGE);

            address.setMessage(addressAddDto.getMessage());
        }

        addressRepository.save(address);
    }

    @Override
    @Transactional
    public void deleteAddress(MypageRequestDTO.AddressDto addressDto, Authentication authentication) {
        User user = getUser(authentication);

        Address address = addressRepository.findById(addressDto.getAddressId())
                .orElseThrow(() -> new CustomException(ErrorStatus.ADDRESS_NOT_FOUND));

        if (!address.getUser().equals(user))
            throw new CustomException(ErrorStatus.ADDRESS_MISMATCH_USER);

        if (address.isDefault() || user.getAddresses().size() == 1)
            throw new CustomException(ErrorStatus.ADDRESS_DEFAULT_LOCKED);

        boolean hasActiveDelivery = deliveryRepository.existsByAddressAndDeliveryStatusIn(
                address, List.of(DeliveryStatus.READY, DeliveryStatus.SHIPPING_EXPIRED));
        if (hasActiveDelivery)
            throw new CustomException(ErrorStatus.ADDRESS_HAS_ACTIVE_DELIVERY);

        addressRepository.delete(address);
    }

    private User getUser(Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));
    }

}
