package com.example.demo.service.general.impl;

import com.example.demo.base.code.exception.CustomException;
import com.example.demo.base.status.ErrorStatus;
import com.example.demo.domain.converter.HomeConverter;
import com.example.demo.domain.dto.Home.HomeRaffleDTO;
import com.example.demo.domain.dto.Home.HomeRaffleListDTO;
import com.example.demo.domain.dto.Search.SearchResponseDTO;
import com.example.demo.entity.Raffle;
import com.example.demo.entity.SearchHistory;
import com.example.demo.entity.User;
import com.example.demo.repository.RaffleRepository;
import com.example.demo.repository.SearchHistoryRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.general.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {

    private final RaffleRepository raffleRepository;
    private final UserRepository userRepository;
    private final SearchHistoryRepository searchHistoryRepository;

    @Override
    @Transactional
    public HomeRaffleListDTO searchRaffles(String keyword, Long userId) {

        // 검색 결과에 따른 List 반환
        List<Raffle> raffles = raffleRepository.findAllByNameContaining(keyword);

        // 로그인 안한 회원인 경우 user를 null로 처리
        User user = null;

        // 로그인 한 회원인 경우 user 불러오기 + 해당 유저의 검색기록 최신화 (이미 존재했던 검색어면 searchCount, searchedAt 최신화)
        if(userId != null){
            user = userRepository.findById(userId)
                    .orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));

            Optional<SearchHistory> searchHistory = searchHistoryRepository.findByKeywordAndUserId(keyword, user.getId());
            if(searchHistory.isEmpty()){
                SearchHistory newSearchHistory = SearchHistory.builder()
                        .user(user)
                        .keyword(keyword)
                        .searchCount(1)
                        .searchedAt(LocalDateTime.now())
                        .build();

                searchHistoryRepository.save(newSearchHistory);
            }
            else{
                SearchHistory prevSearchHistory = searchHistory.get();
                prevSearchHistory.updateSearchHistory();
            }
        }

        List<HomeRaffleDTO> result = convertToHomeRaffleDTOList(raffles, user);

        return HomeRaffleListDTO.builder()
                .raffles(result)
                .build();
    }

    @Override
    public SearchResponseDTO.RecentPopularSearchDTO getRecentPopularSearch(Long userId) {

        List<String> popularSearchResult = new ArrayList<>();
        List<String> recentSearchResult = new ArrayList<>();

        List<SearchHistory> popularSearch = searchHistoryRepository.findTop10UniqueOrderBySearchCountDesc();
        for (SearchHistory searchHistory : popularSearch) {
            popularSearchResult.add(searchHistory.getKeyword());
        }

        // 로그인 한 회원인 경우, 최근 검색어도 표시
        if(userId != null) {
            List<SearchHistory> recentSearch = searchHistoryRepository.findTop10ByUserIdOrderBySearchedAtDesc(userId);
            for (SearchHistory searchHistory : recentSearch) {
                recentSearchResult.add(searchHistory.getKeyword());
            }
        }

        return SearchResponseDTO.RecentPopularSearchDTO.builder()
                .popularSearch(popularSearchResult)
                .recentSearch(recentSearchResult)
                .build();

    }

    @Override
    @Transactional
    public SearchResponseDTO.DeleteRecentSearchDTO deleteRecentSearch(String keyword, Long userId) {
        SearchHistory searchHistory = searchHistoryRepository.findByKeywordAndUserId(keyword, userId)
                .orElseThrow(() -> new CustomException(ErrorStatus.SEARCH_HISTORY_NOT_FOUND));
        searchHistoryRepository.delete(searchHistory);

        return SearchResponseDTO.DeleteRecentSearchDTO
                .builder()
                .deletedKeyword(keyword)
                .build();
    }


    /**
     * 사용하는 메소드 분리
     * */

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

}
