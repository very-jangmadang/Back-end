package com.example.demo.service.general.impl;

import com.example.demo.base.code.exception.CustomException;
import com.example.demo.base.status.ErrorStatus;
import com.example.demo.domain.converter.HomeConverter;
import com.example.demo.domain.converter.base.PageConverter;
import com.example.demo.domain.dto.Home.*;
import com.example.demo.domain.dto.base.PageInfo;
import com.example.demo.entity.*;
import com.example.demo.repository.*;
import com.example.demo.service.general.HomeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.internal.util.stereotypes.Lazy;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class HomeServiceImpl implements HomeService {

    private final RaffleRepository raffleRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;
    private final FollowRepository followRepository;



    @Override
    public HomeResponseDTO getHome(int page, int size) {

        // 마감임박인 래플 5개 조회
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime maxTime = now.plusHours(24);
        Pageable pageableApproaching = PageRequest.of(0, 16);
        Page<Raffle> approachingRaffles = raffleRepository.findRafflesEndingSoon(now, maxTime, pageableApproaching);
        List<Raffle> rafflesSortedByEndAt = approachingRaffles.getContent().stream().limit(5).toList();
        List<HomeRaffleDTO> rafflesSortedByEndAtDTO = convertToHomeRaffleDTOList(rafflesSortedByEndAt, null);

        // 래플 둘러보기 -> 응모자순으로 래플 조회 (응모 안마감된것 우선, 로그인 안 했을 시)
        Pageable pageableMore = PageRequest.of(Math.max(page - 1, 0), size);
        Page<Raffle> moreRaffles = raffleRepository.findAllSortedByApply(now, pageableMore);
        List<Raffle> rafflesSortedByApply = moreRaffles.getContent();

        List<HomeRaffleDTO> rafflesSortedByApplyListDTO = convertToHomeRaffleDTOList(rafflesSortedByApply, null);
        PageInfo pageInfo = PageConverter.toPageInfo(moreRaffles);

        log.info("[HOME] raffles (응모자순) 개수 = {}", rafflesSortedByApplyListDTO.size());
        log.info("[HOME] PageInfo = {}", pageInfo);


        return getHomeResponseDTO(pageInfo, rafflesSortedByEndAtDTO, null, null, rafflesSortedByApplyListDTO);
    }

    @Override
    public HomeResponseDTO getHomeLogin(Long userId, int page, int size){

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));

        // 마감임박인 래플 5개 조회 (로그인 했을 시, 본인이 찜한 여부까지 같이 전달)
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime maxTime = now.plusHours(24);
        Pageable pageableApproaching = PageRequest.of(0, 16);
        Page<Raffle> approachingRaffles = raffleRepository.findRafflesEndingSoon(now, maxTime, pageableApproaching);
        List<Raffle> rafflesSortedByEndAt = approachingRaffles.getContent().stream().limit(5).toList();
        List<HomeRaffleDTO> rafflesSortedByEndAtDTO = convertToHomeRaffleDTOList(rafflesSortedByEndAt, user);


        // 내가 찜한 래플 5개 조회 ( 로그인 했을 시 기능 )
        Pageable pageableForLike = PageRequest.of(0, 16);
        Page<Raffle> pagedLikedRaffles = likeRepository.findRaffleByUserIdOrderByCreatedAtDesc(user.getId(), pageableForLike);
        List<Raffle> likedRaffles = pagedLikedRaffles.getContent().stream().limit(5).toList();
        List<HomeRaffleDTO> myLikeRafflesDTO = convertToHomeRaffleDTOList(likedRaffles, user);


        // 내가 팔로우한 상점의 래플 5개 조회 (마감임박순, 로그인 했을 시 본인의 찜 여부도 전달)
        Pageable pageableForFollow = PageRequest.of(0,16);
        Page<Raffle> pagedFollowingAllRaffles = followRepository.findRafflesByUserFollowings(userId,now, pageableForFollow);
        List<Raffle> myFollowRaffles = pagedFollowingAllRaffles.getContent().stream().limit(5).toList();
        List<HomeRaffleDTO> myFollowingRafflesDTO = convertToHomeRaffleDTOList(myFollowRaffles, user);


        // 래플 둘러보기 -> 응모자순으로 래플 조회 (응모 안마감된것 우선, 로그인 했을 시 찜 여부도 같이 전달)
        Pageable pageableMore = PageRequest.of(Math.max(page - 1, 0), size);
        Page<Raffle> moreRaffles = raffleRepository.findAllSortedByApply(now, pageableMore);
        List<Raffle> rafflesSortedByApply = moreRaffles.getContent();

        List<HomeRaffleDTO> rafflesSortedByApplyListDTO = convertToHomeRaffleDTOList(rafflesSortedByApply, user);
        PageInfo pageInfo = PageConverter.toPageInfo(moreRaffles);

        return getHomeResponseDTO(pageInfo, rafflesSortedByEndAtDTO, myLikeRafflesDTO, myFollowingRafflesDTO, rafflesSortedByApplyListDTO);
    }


    @Override
    public HomeRaffleListDTO getHomeCategories(String categoryName, int page, int size) {

        Category category = categoryRepository.findByName(categoryName)
                .orElseThrow(() -> new CustomException(ErrorStatus.COMMON_WRONG_PARAMETER));

        Pageable pageable = PageRequest.of(page-1, size);
        Page<Raffle> pagedRaffles = raffleRepository.findByCategoryNameSortedByApply(category.getName(), pageable);

        // 카테고리별 조회 + 응모자순으로 래플 조회 (응모 안마감된것 우선)
        List<HomeRaffleDTO> result = convertToHomeRaffleDTOList(pagedRaffles.getContent(), null);
        PageInfo pageInfo = PageConverter.toPageInfo(pagedRaffles);

        return HomeConverter.toHomeRaffleListDTO(result, pageInfo);
    }

    @Override
    public HomeRaffleListDTO getHomeCategoriesLogin(String categoryName, Long userId, int page, int size) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));

        Category category = categoryRepository.findByName(categoryName)
                .orElseThrow(() -> new CustomException(ErrorStatus.COMMON_WRONG_PARAMETER));

        Pageable pageable = PageRequest.of(page-1, size);
        Page<Raffle> pagedRaffles = raffleRepository.findByCategoryNameSortedByApply(category.getName(), pageable);

        // 카테고리별 조회 + 응모자순으로 래플 조회 (응모 안마감된것 우선)
        List<HomeRaffleDTO> result = convertToHomeRaffleDTOList(pagedRaffles.getContent(), user);
        PageInfo pageInfo = PageConverter.toPageInfo(pagedRaffles);

        return HomeConverter.toHomeRaffleListDTO(result, pageInfo);
    }

    @Override
    public HomeRaffleListDTO getHomeApproaching(int page, int size) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime maxTime = now.plusHours(24);
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Raffle> pagedRafflesSortedByEndAt = raffleRepository.findRafflesEndingSoon(now, maxTime, pageable);
        List<Raffle> rafflesSortedByEndAt = pagedRafflesSortedByEndAt.getContent();

        List<HomeRaffleDTO> rafflesSortedByEndAtDTO = convertToHomeRaffleDTOList(rafflesSortedByEndAt, null);
        PageInfo pageInfo = PageConverter.toPageInfo(pagedRafflesSortedByEndAt);

        return HomeConverter.toHomeRaffleListDTO(rafflesSortedByEndAtDTO, pageInfo);
    }

    @Override
    public HomeRaffleListDTO getHomeApproachingLogin(Long userId, int page, int size) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));

        // 마감임박인 래플 더보기 조회
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime maxTime = now.plusHours(24);
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Raffle> pagedRafflesSortedByEndAt = raffleRepository.findRafflesEndingSoon(now, maxTime, pageable);
        List<Raffle> rafflesSortedByEndAt = pagedRafflesSortedByEndAt.getContent();

        List<HomeRaffleDTO> rafflesSortedByEndAtDTO = convertToHomeRaffleDTOList(rafflesSortedByEndAt, user);
        PageInfo pageInfo = PageConverter.toPageInfo(pagedRafflesSortedByEndAt);

        return HomeConverter.toHomeRaffleListDTO(rafflesSortedByEndAtDTO, pageInfo);

    }

    @Override
    public HomeRaffleListDTO getHomeFollowingRaffles(Long userId, int page, int size) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));

        LocalDateTime now = LocalDateTime.now();
        Pageable pageable = PageRequest.of(page-1, size);
        Page<Raffle> pagedFollowingAllRaffles = followRepository.findRafflesByUserFollowings(userId, now, pageable);
        List<Raffle> myFollowRaffles = pagedFollowingAllRaffles.getContent();

        List<HomeRaffleDTO> myFollowingRafflesDTO = convertToHomeRaffleDTOList(myFollowRaffles, user);
        PageInfo pageInfo = PageConverter.toPageInfo(pagedFollowingAllRaffles);

        return HomeConverter.toHomeRaffleListDTO(myFollowingRafflesDTO, pageInfo);
    }

    @Override
    public HomeRaffleListDTO getHomeMoreRaffles(int page, int size) {
        LocalDateTime now = LocalDateTime.now();
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Raffle> pagedRafflesSortedByApply = raffleRepository.findAllSortedByApply(now, pageable);
        List<Raffle> rafflesSortedByApply = pagedRafflesSortedByApply.getContent();

        List<HomeRaffleDTO> rafflesSortedByApplyListDTO = convertToHomeRaffleDTOList(rafflesSortedByApply, null);
        PageInfo pageInfo = PageConverter.toPageInfo(pagedRafflesSortedByApply);

        return HomeConverter.toHomeRaffleListDTO(rafflesSortedByApplyListDTO, pageInfo);
    }

    @Override
    public HomeRaffleListDTO getHomeMoreRafflesLogin(Long userId, int page, int size) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));

        LocalDateTime now = LocalDateTime.now();
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Raffle> pagedRafflesSortedByApply = raffleRepository.findAllSortedByApply(now, pageable);
        List<Raffle> rafflesSortedByApply = pagedRafflesSortedByApply.getContent();

        List<HomeRaffleDTO> rafflesSortedByApplyListDTO = convertToHomeRaffleDTOList(rafflesSortedByApply, user);
        PageInfo pageInfo = PageConverter.toPageInfo(pagedRafflesSortedByApply);

        return HomeConverter.toHomeRaffleListDTO(rafflesSortedByApplyListDTO, pageInfo);
    }

    @Override
    public HomeRaffleListDTO getHomeLikeRaffles(Long userId, int page, int size) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));

        Pageable pageable = PageRequest.of(page-1, size);
        Page<Raffle> pagedLikedRaffles = likeRepository.findRaffleByUserIdOrderByCreatedAtDesc(user.getId(), pageable);
        List<Raffle> likedRaffles = pagedLikedRaffles.getContent();

        List<HomeRaffleDTO> myLikeRafflesDTO = convertToHomeRaffleDTOList(likedRaffles, user);
        PageInfo pageInfo = PageConverter.toPageInfo(pagedLikedRaffles);

        return HomeConverter.toHomeRaffleListDTO(myLikeRafflesDTO, pageInfo);
    }


    /**
     * 사용하는 메소드 분리
    * */


    // HomeResponseDTO 만드는 메소드 분리
    private static HomeResponseDTO getHomeResponseDTO(PageInfo pageInfo, List<HomeRaffleDTO> rafflesSortedByEndAtDTO, List<HomeRaffleDTO> myLikeRafflesDTO,List<HomeRaffleDTO> myFollowingRafflesDTO, List<HomeRaffleDTO> rafflesSortedByApplyListDTO) {
        return HomeResponseDTO.builder()
                .pageInfo(pageInfo)
                .approaching(rafflesSortedByEndAtDTO)
                .myLikeRaffles(myLikeRafflesDTO)
                .myFollowRaffles(myFollowingRafflesDTO)
                .raffles(rafflesSortedByApplyListDTO)
                .build();
    }


    private List<HomeRaffleDTO> convertToHomeRaffleDTOList(List<Raffle> raffles, User user) {

        if (raffles == null || raffles.isEmpty()) {
            log.info("[convertToHomeRaffleDTOList] 입력된 raffles가 없음");
            return Collections.emptyList();
        }

        log.info("[convertToHomeRaffleDTOList] 입력된 raffles 개수 = {}", raffles.size());

        List<Long> raffleIds = raffles.stream()
                .map(Raffle::getId)
                .toList();

        Set<Long> likedRaffleIds;
        if (user != null && !raffleIds.isEmpty()) {
            likedRaffleIds = new HashSet<>(likeRepository.findLikedRaffleIdsByUserAndRaffleList(raffleIds, user));
        } else {
            likedRaffleIds = new HashSet<>();
        }

        return raffles.stream()
                .map(raffle -> {
                    boolean likeStatus = likedRaffleIds.contains(raffle.getId());
                    return HomeConverter.toHomeRaffleDTO(raffle, likeStatus);
                })
                .toList();
    }


}
