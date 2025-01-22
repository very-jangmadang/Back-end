package com.example.demo.jobs;

import com.example.demo.base.code.exception.CustomException;
import com.example.demo.base.status.ErrorStatus;
import com.example.demo.entity.Apply;
import com.example.demo.entity.Raffle;
import com.example.demo.entity.User;
import com.example.demo.entity.base.enums.RaffleStatus;
import com.example.demo.repository.ApplyRepository;
import com.example.demo.repository.RaffleRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.general.EmailService;
import lombok.RequiredArgsConstructor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RaffleEndJob implements Job {

    private final RaffleRepository raffleRepository;
    private final ApplyRepository applyRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    @Override
    @Transactional
    public void execute(JobExecutionContext context) {
        Long raffleId = context.getJobDetail().getJobDataMap().getLong("raffleId");

        Raffle raffle = raffleRepository.findById(raffleId)
                .orElseThrow(() -> new CustomException(ErrorStatus.RAFFLE_NOT_FOUND));

        int applyCount = applyRepository.countByRaffle(raffle);
        List<Apply> applyList = applyRepository.findByRaffle(raffle);

        if (applyCount * raffle.getTicketNum() < raffle.getMinTicket()) {
            raffle.setRaffleStatus(RaffleStatus.UNFULFILLED);
            raffleRepository.save(raffle);

            // 티켓 반환
            int refundTicket = raffle.getTicketNum();

            List<Long> userIds = applyList.stream()
                    .map(apply -> apply.getUser().getId())
                    .collect(Collectors.toList());

            if (!userIds.isEmpty()) {
                userRepository.batchUpdateTicketNum(refundTicket, userIds);
            }

        } else {
            raffle.setRaffleStatus(RaffleStatus.ENDED);
            raffleRepository.save(raffle);

            // 당첨자 추첨
            if (applyList == null || applyList.isEmpty())
                throw new CustomException(ErrorStatus.DRAW_EMPTY);

            Random random = new Random();
            int randomIndex = random.nextInt(applyList.size());

            User winner = applyList.get(randomIndex).getUser();
            raffle.setWinner(winner);
            raffleRepository.save(raffle);

            emailService.sendEmail(winner, raffle);

        }
    }
}