package com.example.demo.jobs;

import com.example.demo.base.code.exception.CustomException;
import com.example.demo.base.status.ErrorStatus;
import com.example.demo.entity.Apply;
import com.example.demo.entity.Raffle;
import com.example.demo.entity.User;
import com.example.demo.entity.base.enums.RaffleStatus;
import com.example.demo.repository.RaffleRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RaffleEndJob implements Job {

    @Autowired
    private RaffleRepository raffleRepository;
    // 관련 코드 PR 상태
    @Autowired
    private ApplyRepository applyRepository;
    @Autowired
    private UserRepository userRepository;

    @Override
    public void execute(JobExecutionContext context) {
        Long raffleId = context.getJobDetail().getJobDataMap().getLong("raffleId");

        Raffle raffle = raffleRepository.findById(raffleId)
                .orElseThrow(() -> new CustomException(ErrorStatus.RAFFLE_NOT_FOUND));

        // 관련 코드 PR 상태
        int applyCount = raffleRepository.countApplyByRaffleId(raffleId);

        if (applyCount < raffle.getMinTicket()) {
            raffle.setRaffleStatus(RaffleStatus.EXPIRED);
            raffleRepository.save(raffle);

            // 티켓 반환
            List<Apply> applyList = applyRepository.findByRaffle(raffle);
            int refundTicket = raffle.getTicketNum();

            for (Apply apply : applyList) {
                User user = apply.getUser();
                user.setTicket_num(user.getTicket_num() - refundTicket);
                userRepository.save(user);
            }

        } else {
            raffle.setRaffleStatus(RaffleStatus.ENDED);
            raffleRepository.save(raffle);

            // 당첨자 추첨 코드
        }
    }
}