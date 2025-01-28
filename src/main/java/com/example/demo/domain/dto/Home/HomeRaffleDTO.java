package com.example.demo.domain.dto.Home;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HomeRaffleDTO {

    private Long raffleId;
    private String imageUrl;
    private String name;
    private int ticketNum;
    private Long timeUntilEnd;
    private boolean finish;
    private int participantNum;
    private boolean like = false;

}
