package com.example.demo.service.general;

import com.example.demo.domain.dto.Home.HomeRaffleListDTO;
import com.example.demo.domain.dto.Home.HomeResponseDTO;
import com.example.demo.entity.Raffle;
import org.springframework.data.domain.Page;

public interface HomeService {

    HomeResponseDTO getHome(int page, int size);

    HomeResponseDTO getHomeLogin(Long userId, int page, int size);

    HomeRaffleListDTO getHomeCategories(String categoryName, int page, int size);

    HomeRaffleListDTO getHomeCategoriesLogin(String categoryName, Long userId, int page, int size);

    HomeRaffleListDTO getHomeApproaching(int page, int size);

    HomeRaffleListDTO getHomeApproachingLogin(Long userId, int page, int size);

    HomeRaffleListDTO getHomeFollowingRaffles(Long userId, int page, int size);

    HomeRaffleListDTO getHomeMoreRaffles(int page, int size);

    HomeRaffleListDTO getHomeMoreRafflesLogin(Long userId, int page, int size);

    HomeRaffleListDTO getHomeLikeRaffles(Long userId, int page, int size);

}
