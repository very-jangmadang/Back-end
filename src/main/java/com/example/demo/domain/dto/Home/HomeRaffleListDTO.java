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
public class HomeRaffleListDTO {
    private List<HomeRaffleDTO> raffles;
    private int currentPage;
    private int totalPages;
    private long totalElements;
    private boolean hasNext;
}