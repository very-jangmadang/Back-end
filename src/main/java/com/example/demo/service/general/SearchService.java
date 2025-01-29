package com.example.demo.service.general;

import com.example.demo.domain.dto.Home.HomeRaffleListDTO;

public interface SearchService {

    HomeRaffleListDTO searchRaffles(String keyword, Long userId);

}
