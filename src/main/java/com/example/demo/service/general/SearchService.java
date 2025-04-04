package com.example.demo.service.general;

import com.example.demo.domain.dto.Home.HomeRaffleListDTO;
import com.example.demo.domain.dto.Search.SearchResponseDTO;

public interface SearchService {

    SearchResponseDTO.SearchRaffleListDTO searchRaffles(String keyword, Long userId, int page, int size);

    SearchResponseDTO.RecentPopularSearchDTO getRecentPopularSearch(Long userId);

    SearchResponseDTO.DeleteRecentSearchDTO deleteRecentSearch(String keyword, Long userId);
}
