package com.example.demo.service.general.impl;

import com.example.demo.base.code.exception.CustomException;
import com.example.demo.base.status.ErrorStatus;
import com.example.demo.domain.converter.HomeConverter;
import com.example.demo.domain.dto.Home.*;
import com.example.demo.entity.*;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.RaffleRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.general.HomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class HomeServiceImpl implements HomeService {

    private final RaffleRepository raffleRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    @Override
    public HomeResponseDTO getHome() {

        List<Raffle> raffles = raffleRepository.findAll();

        // 마감임박인 래플 5개 조회 (로그인 안 했을 시)
        List<Raffle> rafflesSortedByEndAt = sortRafflesByEndAt24(raffles, 5);

        List<HomeRaffleDTO> rafflesSortedByEndAtDTO = convertToHomeRaffleDTOList(rafflesSortedByEndAt, null);

        // 래플 둘러보기 -> 응모자순으로 래플 조회 (응모 안마감된것 우선, 로그인 안 했을 시)
        List<Raffle> rafflesSortedByApplyList = sortRafflesByApply(raffles);
        List<HomeRaffleDTO> rafflesSortedByApplyListDTO = convertToHomeRaffleDTOList(rafflesSortedByApplyList, null);

        return getHomeResponseDTO(rafflesSortedByEndAtDTO, null, null, rafflesSortedByApplyListDTO);
    }

    @Override
    public HomeResponseDTO getHomeLogin(Long userId){

        List<Raffle> raffles = raffleRepository.findAll();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));
        LocalDateTime now = LocalDateTime.now();

        // 마감임박인 래플 5개 조회 (로그인 했을 시, 본인이 찜한 여부까지 같이 전달)
        List<Raffle> rafflesSortedByEndAt = sortRafflesByEndAt24(raffles, 5);
        List<HomeRaffleDTO> rafflesSortedByEndAtDTO = convertToHomeRaffleDTOList(rafflesSortedByEndAt, user);


        // 내가 찜한 래플 5개 조회 ( 로그인 했을 시 기능 )
        List<Like> sortedLikes = user.getLikes().stream()
                .sorted(Comparator.comparing(Like::getCreatedAt).reversed())
                .limit(5)
                .toList();

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
        List<Raffle> rafflesSortedByApplyList = sortRafflesByApply(raffles);
        List<HomeRaffleDTO> rafflesSortedByApplyListDTO = convertToHomeRaffleDTOList(rafflesSortedByApplyList, user);


        return getHomeResponseDTO(rafflesSortedByEndAtDTO, myLikeRafflesDTO, myFollowingRafflesDTO, rafflesSortedByApplyListDTO);
    }


    @Override
    public HomeRaffleListDTO getHomeCategories(String categoryName) {

        Category category = categoryRepository.findByName(categoryName)
                .orElseThrow(() -> new CustomException(ErrorStatus.COMMON_WRONG_PARAMETER));

        List<Raffle> raffles = raffleRepository.findByCategoryName(category.getName());

        // 카테고리별 조회 + 응모자순으로 래플 조회 (응모 안마감된것 우선)
        List<Raffle> rafflesSortedByApplyList = sortRafflesByApply(raffles);
        List<HomeRaffleDTO> result = convertToHomeRaffleDTOList(rafflesSortedByApplyList, null);

        return HomeRaffleListDTO.builder()
                .raffles(result).build();
    }

    @Override
    public HomeRaffleListDTO getHomeCategoriesLogin(String categoryName, Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));

        Category category = categoryRepository.findByName(categoryName)
                .orElseThrow(() -> new CustomException(ErrorStatus.COMMON_WRONG_PARAMETER));

        List<Raffle> raffles = raffleRepository.findByCategoryName(category.getName());

        // 카테고리별 조회 + 응모자순으로 래플 조회 (응모 안마감된것 우선)
        List<Raffle> rafflesSortedByApplyList = sortRafflesByApply(raffles);
        List<HomeRaffleDTO> result = convertToHomeRaffleDTOList(rafflesSortedByApplyList, user);

        return HomeRaffleListDTO.builder()
                .raffles(result).build();
    }

    @Override
    public HomeRaffleListDTO getHomeApproaching() {

        List<Raffle> raffles = raffleRepository.findAll();

        // 마감임박인 래플 더보기 조회
        List<Raffle> rafflesSortedByEndAt = sortRafflesByEndAt24(raffles, null);
        List<HomeRaffleDTO> rafflesSortedByEndAtDTO = convertToHomeRaffleDTOList(rafflesSortedByEndAt, null);

        return HomeRaffleListDTO.builder()
                .raffles(rafflesSortedByEndAtDTO)
                .build();
    }

    @Override
    public HomeRaffleListDTO getHomeApproachingLogin(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));

        List<Raffle> raffles = raffleRepository.findAll();

        // 마감임박인 래플 더보기 조회
        List<Raffle> rafflesSortedByEndAt = sortRafflesByEndAt24(raffles, null);
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
        List<Raffle> raffles = raffleRepository.findAll();
        List<Raffle> rafflesSortedByApplyList = sortRafflesByApply(raffles);
        List<HomeRaffleDTO> rafflesSortedByApplyListDTO = convertToHomeRaffleDTOList(rafflesSortedByApplyList, null);

        return HomeRaffleListDTO.builder()
                .raffles(rafflesSortedByApplyListDTO)
                .build();
    }

    @Override
    public HomeRaffleListDTO getHomeMoreRafflesLogin(Long userId) {
        List<Raffle> raffles = raffleRepository.findAll();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));

        List<Raffle> rafflesSortedByApplyList = sortRafflesByApply(raffles);
        List<HomeRaffleDTO> rafflesSortedByApplyListDTO = convertToHomeRaffleDTOList(rafflesSortedByApplyList, user);

        return HomeRaffleListDTO.builder()
                .raffles(rafflesSortedByApplyListDTO)
                .build();
    }

    @Override
    public HomeRaffleListDTO getHomeLikeRaffles(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));

        List<Like> sortedLikes = user.getLikes().stream()
                .sorted(Comparator.comparing(Like::getCreatedAt).reversed())
                .toList();

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

    // 응모 마감 안된것 + 응모자순으로 정렬하는 로직
    private List<Raffle> sortRafflesByApply(List<Raffle> raffles) {
        LocalDateTime now = LocalDateTime.now();
        return Stream.concat(
                raffles.stream()
                        .filter(r -> Duration.between(now, r.getEndAt()).toMillis() >= 0)
                        .sorted((r1, r2) -> Integer.compare(
                                r2.getApplyList() != null ? r2.getApplyList().size() : 0,
                                r1.getApplyList() != null ? r1.getApplyList().size() : 0
                        )),
                raffles.stream()
                        .filter(r -> Duration.between(now, r.getEndAt()).toMillis() < 0)
                        .sorted((r1, r2) -> Integer.compare(
                                r2.getApplyList() != null ? r2.getApplyList().size() : 0,
                                r1.getApplyList() != null ? r1.getApplyList().size() : 0
                        ))
        ).toList();
    }

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

    private List<Raffle> sortRafflesByEndAt24(List<Raffle> raffles, Integer limit){
        LocalDateTime now = LocalDateTime.now();

        if(limit != null){
            return raffles.stream()
                    .filter(r -> Duration.between(now, r.getEndAt()).toMillis() >= 0 &&
                            Duration.between(now, r.getEndAt()).toHours() <= 24)
                    .sorted(Comparator.comparingLong(r -> Duration.between(now, r.getEndAt()).toMillis()))
                    .limit(limit)
                    .toList();
        }

        else{
            return raffles.stream()
                    .filter(r -> Duration.between(now, r.getEndAt()).toMillis() >= 0 &&
                            Duration.between(now, r.getEndAt()).toHours() <= 24)
                    .sorted(Comparator.comparingLong(r -> Duration.between(now, r.getEndAt()).toMillis()))
                    .toList();
        }

    }

}
