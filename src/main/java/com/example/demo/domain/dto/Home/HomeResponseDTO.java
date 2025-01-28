package com.example.demo.domain.dto.Home;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HomeResponseDTO {

    private List<HomeRaffleDTO> approaching;
    private List<HomeRaffleDTO> myLikeRaffles;
    private List<HomeRaffleDTO> myFollowRaffles;
    private List<HomeRaffleDTO> raffles;

}
