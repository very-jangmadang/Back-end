package com.example.demo.service.general;

import com.example.demo.domain.dto.HomeResponseDTO;

public interface HomeService {

    HomeResponseDTO getHome();

    HomeResponseDTO getHomeCategories(String categoryName);

}
