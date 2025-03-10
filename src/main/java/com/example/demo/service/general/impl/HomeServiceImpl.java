package com.example.demo.service.general.impl;

import com.example.demo.base.code.exception.CustomException;
import com.example.demo.base.status.ErrorStatus;
import com.example.demo.domain.converter.HomeConverter;
import com.example.demo.domain.dto.Home.*;
import com.example.demo.entity.*;
import com.example.demo.repository.*;
import com.example.demo.service.general.HomeService;
import lombok.RequiredArgsConstructor;
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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class HomeServiceImpl implements HomeService {

    private final RaffleRepository raffleRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;
    @Lazy
    private final HomeService homeService;

    @Override
    @Cacheable(value = "homeRaffles", key = "'approaching-' + #page")
    public Page<Raffle> getApproachingRaffles(int page, int size) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime maxTime = now.plusHours(24);

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "endAt"));
        return raffleRepository.findRafflesEndingSoon(now, maxTime, pageable);
    }

    @Override
    @Cacheable(value = "homeRaffles", key="'apply-' + #page")
    public Page<Raffle> getMoreRaffles(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return raffleRepository.findAllSortedByApply(pageable);
    }


    @Override
    public HomeResponseDTO getHome() {

        Page<Raffle> approachingRaffles = homeService.getApproachingRaffles(0, 16);
        List<Raffle> rafflesSortedByEndAt = approachingRaffles.getContent().stream().limit(5).toList();
        List<HomeRaffleDTO> rafflesSortedByEndAtDTO = convertToHomeRaffleDTOList(rafflesSortedByEndAt, null);

        // 래플 둘러보기 -> 응모자순으로 래플 조회 (응모 안마감된것 우선, 로그인 안 했을 시)
        Page<Raffle> applyRaffles = homeService.getMoreRaffles(0, 16);
        List<Raffle> rafflesSortedByApply = applyRaffles.getContent().stream().limit(5).toList();
        List<HomeRaffleDTO> rafflesSortedByApplyListDTO = convertToHomeRaffleDTOList(rafflesSortedByApply, null);

        return getHomeResponseDTO(rafflesSortedByEndAtDTO, null, null, rafflesSortedByApplyListDTO);
    }

    @Override
    public HomeResponseDTO getHomeLogin(Long userId){

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));
        LocalDateTime now = LocalDateTime.now();

        // 마감임박인 래플 5개 조회 (로그인 했을 시, 본인이 찜한 여부까지 같이 전달)
        Page<Raffle> approachingRaffles = homeService.getApproachingRaffles(0, 16);
        List<Raffle> rafflesSortedByEndAt = approachingRaffles.getContent().stream().limit(5).toList();
        List<HomeRaffleDTO> rafflesSortedByEndAtDTO = convertToHomeRaffleDTOList(rafflesSortedByEndAt, user);


        // 내가 찜한 래플 5개 조회 ( 로그인 했을 시 기능 )
        Pageable pageable = PageRequest.of(0, 16);
        Page<Like> likes = likeRepository.findByUserIdOrderByCreatedAtDesc(user.getId(), pageable);
        List<Like> sortedLikes = likes.getContent().stream().limit(5).toList();

        List<HomeRaffleDTO> myLikeRafflesDTO = new ArrayList<>();

        for (Like sortedLike : sortedLikes) {
            Raffle raffle = sortedLike.getRaffle();
            HomeRaffleDTO raffleDTO = HomeConverter.toHomeRaffleDTO(raffle, true);
            myLikeRafflesDTO.add(raffleDTO);
        }

        // 내가 팔로우한 상점의 래플 5개 조회 (마감임박순, 로그인 했을 시 본인의 찜 여부도 전달)
        List<Follow> followings = user.getFollowings();
        List<Raffle> followingAllRaffles = new ArrayList<>();


        for (Follow following : followings) {
            if (following.getStoreId() != null) {
                List<Raffle> storeRaffles = raffleRepository.findAllByUserId(following.getStoreId());
                followingAllRaffles.addAll(storeRaffles);
            }
        }

        List<Raffle> myFollowRaffles = sortRafflesByEndAt(followingAllRaffles, 5);
        List<HomeRaffleDTO> myFollowingRafflesDTO = convertToHomeRaffleDTOList(myFollowRaffles, user);


        // 래플 둘러보기 -> 응모자순으로 래플 조회 (응모 안마감된것 우선, 로그인 했을 시 찜 여부도 같이 전달)
        Page<Raffle> applyRaffles = homeService.getMoreRaffles(0, 16);
        List<Raffle> rafflesSortedByApply = applyRaffles.getContent().stream().limit(5).toList();
        List<HomeRaffleDTO> rafflesSortedByApplyListDTO = convertToHomeRaffleDTOList(rafflesSortedByApply, user);


        return getHomeResponseDTO(rafflesSortedByEndAtDTO, myLikeRafflesDTO, myFollowingRafflesDTO, rafflesSortedByApplyListDTO);
    }


    @Override
    public HomeRaffleListDTO getHomeCategories(String categoryName, int page, int size) {

        Category category = categoryRepository.findByName(categoryName)
                .orElseThrow(() -> new CustomException(ErrorStatus.COMMON_WRONG_PARAMETER));

        Pageable pageable = PageRequest.of(page-1, size);
        Page<Raffle> pagedRaffles = raffleRepository.findByCategoryNameSortedByApply(category.getName(), pageable);

        // 카테고리별 조회 + 응모자순으로 래플 조회 (응모 안마감된것 우선)
        List<HomeRaffleDTO> result = convertToHomeRaffleDTOList(pagedRaffles.getContent(), null);

        return HomeRaffleListDTO.builder()
                .raffles(result).build();
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

        return HomeRaffleListDTO.builder()
                .raffles(result).build();
    }

    @Override
    public HomeRaffleListDTO getHomeApproaching(int page, int size) {
        Page<Raffle> pagedRafflesSortedByEndAt = homeService.getApproachingRaffles(page-1, size);
        List<Raffle> rafflesSortedByEndAt = pagedRafflesSortedByEndAt.getContent();
        List<HomeRaffleDTO> rafflesSortedByEndAtDTO = convertToHomeRaffleDTOList(rafflesSortedByEndAt, null);

        return HomeRaffleListDTO.builder()
                .raffles(rafflesSortedByEndAtDTO)
                .build();
    }

    @Override
    public HomeRaffleListDTO getHomeApproachingLogin(Long userId, int page, int size) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));

        // 마감임박인 래플 더보기 조회
        Page<Raffle> pagedRafflesSortedByEndAt = homeService.getApproachingRaffles(page-1, size);
        List<Raffle> rafflesSortedByEndAt = pagedRafflesSortedByEndAt.getContent();
        List<HomeRaffleDTO> rafflesSortedByEndAtDTO = convertToHomeRaffleDTOList(rafflesSortedByEndAt, user);

        return HomeRaffleListDTO.builder()
                .raffles(rafflesSortedByEndAtDTO)
                .build();

    }

    @Override
    public HomeRaffleListDTO getHomeFollowingRaffles(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));

        List<Follow> followings = user.getFollowings();
        List<Raffle> followingAllRaffles = new ArrayList<>();


        for (Follow following : followings) {
            if (following.getStoreId() != null) {
                List<Raffle> storeRaffles = raffleRepository.findAllByUserId(following.getStoreId());
                followingAllRaffles.addAll(storeRaffles);
            }
        }

        List<Raffle> myFollowRaffles = sortRafflesByEndAt(followingAllRaffles, 5);
        List<HomeRaffleDTO> myFollowingRafflesDTO = convertToHomeRaffleDTOList(myFollowRaffles, user);

        return HomeRaffleListDTO.builder()
                .raffles(myFollowingRafflesDTO)
                .build();
    }

    @Override
    public HomeRaffleListDTO getHomeMoreRaffles() {
        Page<Raffle> applyRaffles = homeService.getMoreRaffles(0, 16);
        List<Raffle> rafflesSortedByApply = applyRaffles.getContent().stream().limit(5).toList();
        List<HomeRaffleDTO> rafflesSortedByApplyListDTO = convertToHomeRaffleDTOList(rafflesSortedByApply, null);

        return HomeRaffleListDTO.builder()
                .raffles(rafflesSortedByApplyListDTO)
                .build();
    }

    @Override
    public HomeRaffleListDTO getHomeMoreRafflesLogin(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));

        Page<Raffle> applyRaffles = homeService.getMoreRaffles(0, 16);
        List<Raffle> rafflesSortedByApply = applyRaffles.getContent().stream().limit(5).toList();
        List<HomeRaffleDTO> rafflesSortedByApplyListDTO = convertToHomeRaffleDTOList(rafflesSortedByApply, user);

        return HomeRaffleListDTO.builder()
                .raffles(rafflesSortedByApplyListDTO)
                .build();
    }

    @Override
    public HomeRaffleListDTO getHomeLikeRaffles(Long userId, int page, int size) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));

        Pageable pageable = PageRequest.of(page-1, size);
        Page<Like> likes = likeRepository.findByUserIdOrderByCreatedAtDesc(user.getId(), pageable);
        List<Like> sortedLikes = likes.getContent();

        List<HomeRaffleDTO> myLikeRafflesDTO = new ArrayList<>();

        for (Like sortedLike : sortedLikes) {
            Raffle raffle = sortedLike.getRaffle();
            HomeRaffleDTO raffleDTO = HomeConverter.toHomeRaffleDTO(raffle, true);
            myLikeRafflesDTO.add(raffleDTO);
        }

        return HomeRaffleListDTO.builder()
                .raffles(myLikeRafflesDTO)
                .build();
    }


    /**
     * 사용하는 메소드 분리
    * */


    // HomeResponseDTO 만드는 메소드 분리
    private static HomeResponseDTO getHomeResponseDTO(List<HomeRaffleDTO> rafflesSortedByEndAtDTO, List<HomeRaffleDTO> myLikeRafflesDTO,List<HomeRaffleDTO> myFollowingRafflesDTO, List<HomeRaffleDTO> rafflesSortedByApplyListDTO) {
        return HomeResponseDTO.builder()
                .approaching(rafflesSortedByEndAtDTO)
                .myLikeRaffles(myLikeRafflesDTO)
                .myFollowRaffles(myFollowingRafflesDTO)
                .raffles(rafflesSortedByApplyListDTO)
                .build();
    }

    private List<HomeRaffleDTO> convertToHomeRaffleDTOList(List<Raffle> raffles, User user) {
        List<HomeRaffleDTO> dtoList = new ArrayList<>();

        for (Raffle raffle : raffles) {
            boolean likeStatus = false;

            // 로그인한 경우에만 likeStatus 조회
            if (user != null) {
                List<Long> likedRaffleIds = user.getLikes().stream()
                        .map(like -> like.getRaffle().getId())
                        .toList();
                likeStatus = likedRaffleIds.contains(raffle.getId());
            }

            HomeRaffleDTO raffleDTO = HomeConverter.toHomeRaffleDTO(raffle, likeStatus);
            dtoList.add(raffleDTO);
        }

        return dtoList;
    }

    private List<Raffle> sortRafflesByEndAt(List<Raffle> raffles, Integer limit){

        LocalDateTime now = LocalDateTime.now();

        if(limit != null){
            return raffles.stream()
                    .filter(r -> Duration.between(now, r.getEndAt()).toMillis() >= 0)
                    .sorted(Comparator.comparingLong(r -> Duration.between(now, r.getEndAt()).toMillis()))
                    .limit(limit)
                    .toList();
        }

        else{
            return raffles.stream()
                    .filter(r -> Duration.between(now, r.getEndAt()).toMillis() >= 0)
                    .sorted(Comparator.comparingLong(r -> Duration.between(now, r.getEndAt()).toMillis()))
                    .toList();
        }

    }

}
