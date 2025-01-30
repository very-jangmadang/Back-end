package com.example.demo.service.general;

import com.example.demo.domain.dto.Home.HomeRaffleListDTO;
import com.example.demo.domain.dto.Search.SearchResponseDTO;

public interface SearchService {

    HomeRaffleListDTO searchRaffles(String keyword, Long userId);

    SearchResponseDTO getRecentPopularSearch(Long userId);
}
