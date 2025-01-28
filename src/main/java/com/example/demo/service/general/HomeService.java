package com.example.demo.service.general;

import com.example.demo.domain.dto.Home.HomeApproachingResponseDTO;
import com.example.demo.domain.dto.Home.HomeCategoryResponseDTO;
import com.example.demo.domain.dto.Home.HomeRaffleListDTO;
import com.example.demo.domain.dto.Home.HomeResponseDTO;

public interface HomeService {

    HomeResponseDTO getHome();

    HomeResponseDTO getHomeLogin(String email);

    HomeRaffleListDTO getHomeCategories(String categoryName);

    HomeRaffleListDTO getHomeCategoriesLogin(String categoryName, String email);

    HomeRaffleListDTO getHomeApproaching();
}
