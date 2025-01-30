package com.example.demo.service.general;

import com.example.demo.domain.dto.Home.HomeRaffleListDTO;
import com.example.demo.domain.dto.Home.HomeResponseDTO;

public interface HomeService {

    HomeResponseDTO getHome();

    HomeResponseDTO getHomeLogin(Long userId);

    HomeRaffleListDTO getHomeCategories(String categoryName);

    HomeRaffleListDTO getHomeCategoriesLogin(String categoryName, Long userId);

    HomeRaffleListDTO getHomeApproaching();

    HomeRaffleListDTO getHomeApproachingLogin(Long userId);

    HomeRaffleListDTO getHomeFollowingRaffles(Long userId);

    HomeRaffleListDTO getHomeMoreRaffles();

    HomeRaffleListDTO getHomeMoreRafflesLogin(Long userId);

    HomeRaffleListDTO getHomeLikeRaffles(Long userId);
}
