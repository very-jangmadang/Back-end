package com.example.demo.service.general;

import com.example.demo.domain.dto.Home.HomeCategoryResponseDTO;
import com.example.demo.domain.dto.Home.HomeResponseDTO;

public interface HomeService {

    HomeResponseDTO getHome();

    HomeResponseDTO getHomeLogin(String email);

    HomeCategoryResponseDTO getHomeCategories(String categoryName);

    HomeCategoryResponseDTO getHomeCategoriesLogin(String categoryName, String email);
}
