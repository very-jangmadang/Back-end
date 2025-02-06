package com.example.demo.domain.dto.Home;

import com.example.demo.entity.Raffle;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HomeRaffleDTO {

    private Long raffleId;
    private List<String> imageUrls;
    private String name;
    private int ticketNum;
    private Long timeUntilEnd;
    private boolean finish;
    private int participantNum;
    private boolean like = false;

}
