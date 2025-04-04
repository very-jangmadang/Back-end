package com.example.demo.domain.dto.Search;

import com.example.demo.domain.dto.Home.HomeRaffleDTO;
import com.example.demo.domain.dto.base.PageInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;


public class SearchResponseDTO {

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RecentPopularSearchDTO{
        private List<String> recentSearch;
        private List<String> popularSearch;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DeleteRecentSearchDTO{
        private String deletedKeyword;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SearchRaffleListDTO{
        private PageInfo pageInfo;
        private List<HomeRaffleDTO> searchedRaffles;
    }


}
