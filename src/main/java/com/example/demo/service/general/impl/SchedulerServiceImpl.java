package com.example.demo.service.general.impl;

import com.example.demo.base.Constants;
import com.example.demo.base.code.exception.CustomException;
import com.example.demo.base.status.ErrorStatus;
import com.example.demo.domain.dto.Scheduler.SchedulerResponseDTO;
import com.example.demo.entity.Delivery;
import com.example.demo.entity.Raffle;
import com.example.demo.entity.base.enums.DeliveryStatus;
import com.example.demo.entity.base.enums.RaffleStatus;
import com.example.demo.jobs.*;
import com.example.demo.repository.DeliveryRepository;
import com.example.demo.repository.RaffleRepository;
import com.example.demo.service.general.SchedulerService;
import lombok.RequiredArgsConstructor;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class SchedulerServiceImpl implements SchedulerService {

    private final Scheduler scheduler;
    private final RaffleRepository raffleRepository;
    private final DeliveryRepository deliveryRepository;

    private void scheduleJob(String jobName, Class<? extends Job> jobClass, LocalDateTime time, Map<String, Object> jobData) {
        try {
            if (!scheduler.isStarted())
                scheduler.start();

            JobDetail jobDetail = buildJobDetail(jobName, jobClass, jobData);
            Trigger trigger = buildJobTrigger(time);

            System.out.println(jobName);
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException e) {
            handleSchedulerException(e);
        }
    }

    private JobDetail buildJobDetail(String jobName, Class<? extends Job> jobClass, Map<String, Object> jobData) {
        JobDataMap dataMap = new JobDataMap();

        if (jobData != null)
            dataMap.putAll(jobData);

        return JobBuilder.newJob(jobClass)
                .withIdentity(jobName)
                .usingJobData(dataMap)
                .storeDurably()
                .build();
    }

    private Trigger buildJobTrigger(LocalDateTime time) {
        ZoneId appZoneId = TimeZone.getDefault().toZoneId();

        LocalDateTime adjustedTime = time.withSecond(0).withNano(0);
        Date startDate = Date.from(adjustedTime.atZone(appZoneId).toInstant());

        Date now = new Date();
        if (startDate.before(now))
            return TriggerBuilder.newTrigger()
                    .startNow()
                    .build();

        return TriggerBuilder.newTrigger()
                .startAt(startDate)
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withMisfireHandlingInstructionFireNow())
                .build();
    }

    private void handleSchedulerException(SchedulerException e) {
        Throwable cause = e.getCause();
        if (cause instanceof JobPersistenceException) {
            throw new CustomException(ErrorStatus.JOB_STORE_FAILED);
        } else if (cause instanceof JobExecutionException) {
            throw new CustomException(ErrorStatus.JOB_EXECUTION_FAILED);
        } else {
            throw new CustomException(ErrorStatus.JOB_UNKNOWN);
        }
    }

    @Override
    public void scheduleRaffleJob(Raffle raffle) {
        if (raffle.getRaffleStatus() == RaffleStatus.UNOPENED) {
            String startJobName = "Raffle_" + raffle.getId() + "_START";
            LocalDateTime startTime = raffle.getStartAt();
            scheduleJob(startJobName, RaffleStartJob.class, startTime, Map.of("raffleId", raffle.getId()));
        }

        String endJobName = "Raffle_" + raffle.getId() + "_END";
        LocalDateTime endTime = raffle.getEndAt();
        scheduleJob(endJobName, RaffleEndJob.class, endTime, Map.of("raffleId", raffle.getId()));
    }

    @Override
    public void scheduleDrawJob(Raffle raffle) {
        String jobName = "Raffle_" + raffle.getId() + "_DRAW";
        scheduleJob(jobName, DrawJob.class, raffle.getEndAt().plusHours(Constants.CHOICE_PERIOD), Map.of("raffleId", raffle.getId()));
    }

    @Override
    public void scheduleDeliveryJob(Delivery delivery) {
        String jobName = "Delivery_" + delivery.getId();
        LocalDateTime triggerTime;
        Class<? extends Job> jobClass;

        switch (delivery.getDeliveryStatus()) {
            case WAITING_ADDRESS:
                jobName += "_Address";
                jobClass = AddressJob.class;
                triggerTime = delivery.getAddressDeadline();
            break;
            case READY:
                jobName += "_Shipping";
                jobClass = ShippingJob.class;
                triggerTime = delivery.getShippingDeadline();
                break;
            case SHIPPED:
                jobName += "_Complete";
                jobClass = CompleteJob.class;
                triggerTime = LocalDateTime.now().plusHours(Constants.COMPLETE);
                break;
            case ADDRESS_EXPIRED:
                jobName += "_Waiting";
                jobClass = WaitingJob.class;
                triggerTime = delivery.getAddressDeadline().plusHours(Constants.CHOICE_PERIOD);
                break;
            case SHIPPING_EXPIRED:
                jobName += "_Waiting";
                jobClass = WaitingJob.class;
                triggerTime = delivery.getShippingDeadline().plusHours(Constants.CHOICE_PERIOD);
                break;
            default:
                throw new CustomException(ErrorStatus.INVALID_DELIVERY_STATUS);
        }

        scheduleJob(jobName, jobClass, triggerTime, Map.of("deliveryId", delivery.getId()));
    }

    public void cancelJob(String jobName) {
        try {
            JobKey jobKey = JobKey.jobKey(jobName);
            TriggerKey triggerKey = TriggerKey.triggerKey(jobName);

            if (scheduler.checkExists(jobKey)) {
                scheduler.unscheduleJob(triggerKey);
                scheduler.deleteJob(jobKey);
            }
            // 존재하지 않는 작업 취소 불가
//            else
//                throw new CustomException(ErrorStatus.JOB_NOT_FOUND);

        } catch (SchedulerException e) {
            throw new CustomException(ErrorStatus.JOB_CANCEL_FAILED);
        }
    }

    @Override
    public void cancelDrawJob(Raffle raffle) {
        cancelJob("Raffle_" + raffle.getId() + "_DRAW");
    }

    @Override
    public void cancelDeliveryJob(Delivery delivery, String type) {
        cancelJob("Delivery_" + delivery.getId() + "_" + type);
    }

    @Override
    public void cancelRaffleJob(Raffle raffle) {
        String jobName = "Raffle_" + raffle.getId();
        cancelJob(jobName + "_END");

        if (raffle.getRaffleStatus() == RaffleStatus.UNOPENED)
            cancelJob(jobName + "_START");
    }

    @Override
    public SchedulerResponseDTO getJobKeys() {
        Set<JobKey> jobKeys = new HashSet<>();

        try {
            jobKeys = scheduler.getJobKeys(GroupMatcher.anyGroup());
        } catch (SchedulerException e) {
            handleSchedulerException(e);
        }

        return SchedulerResponseDTO.builder()
                .JobKeys(jobKeys)
                .build();
    }

    @Override
    public void scheduleAll() {
        try {
            if (!scheduler.isStarted())
                scheduler.start();

            if (!scheduler.getJobKeys(GroupMatcher.anyGroup()).isEmpty())
                return;

            List<Raffle> raffleList = raffleRepository.findByRaffleStatusIn(List.of(
                    RaffleStatus.UNOPENED, RaffleStatus.ACTIVE, RaffleStatus.UNFULFILLED));

            for (Raffle raffle : raffleList) {
                switch (raffle.getRaffleStatus()) {
                    case UNOPENED:
                    case ACTIVE:
                        scheduleRaffleJob(raffle);
                        break;
                    case UNFULFILLED:
                        scheduleDrawJob(raffle);
                        break;
                }
            }

            raffleList = raffleRepository.findByRaffleStatusIn(List.of(RaffleStatus.ENDED));

            for (Raffle raffle : raffleList) {
                Delivery delivery = deliveryRepository.findByRaffleAndDeliveryStatusIn(raffle, List.of(
                        DeliveryStatus.WAITING_ADDRESS, DeliveryStatus.READY, DeliveryStatus.SHIPPED,
                        DeliveryStatus.ADDRESS_EXPIRED, DeliveryStatus.SHIPPING_EXPIRED));

                if (delivery == null)
                    continue;

                switch (delivery.getDeliveryStatus()) {
                    case WAITING_ADDRESS:
                    case READY:
                    case ADDRESS_EXPIRED:
                    case SHIPPING_EXPIRED:
                        scheduleDeliveryJob(delivery);
                        break;

                    case SHIPPED:
                        String jobName = "Delivery_" + delivery.getId() + "_Complete";
                        LocalDateTime triggerTime = delivery.getUpdatedAt().withSecond(0).withNano(0)
                                .plusHours(Constants.COMPLETE);
                        Class<? extends Job> jobClass = CompleteJob.class;

                        scheduleJob(jobName, jobClass, triggerTime, Map.of("deliveryId", delivery.getId()));
                        break;
                }
            }

        } catch (SchedulerException e) {
            handleSchedulerException(e);
        }
    }

    @Override
    public void scheduleNew(Long raffleId) {
        try {
            if (!scheduler.isStarted())
                scheduler.start();

            List<Raffle> raffleList = raffleRepository.findByIdGreaterThanEqualOrderByIdAsc(raffleId);

            for (Raffle raffle : raffleList) {
                switch (raffle.getRaffleStatus()) {
                    case UNOPENED:
                    case ACTIVE:
                        scheduleRaffleJob(raffle);
                        break;
                    case UNFULFILLED:
                        scheduleDrawJob(raffle);
                        break;
                }
            }

        } catch (SchedulerException e) {
            handleSchedulerException(e);
        }
    }

    @Override
    public void scheduleInvoiceCheckJob(Delivery delivery) {

        // 1. 개최자에게 마감 1시간 전 알림
        String hostJobName = "HostInvoiceCheck_" + delivery.getId();
        Class<? extends Job> hostJobClass = InvoiceCheckJob.class;
        LocalDateTime hostTriggerTime = delivery.getShippingDeadline().minusHours(1);
        scheduleJob(hostJobName, hostJobClass, hostTriggerTime, Map.of("deliveryId", delivery.getId()));

        // 2. 당첨자에게 마감 이후 알림
        String winnerJobName = "WinnerInvoiceOver_" + delivery.getId();
        Class<? extends Job> winnerJobClass = InvoiceOverJob.class;
        LocalDateTime winnerTriggerTime = delivery.getShippingDeadline();
        scheduleJob(winnerJobName, winnerJobClass, winnerTriggerTime, Map.of("deliveryId", delivery.getId()));
    }

    @Override
    public void scheduleAddressCheckJob(Delivery delivery){

        // 당첨자에게 주소마감 1시간 전 알림
        String checkJobName = "WinnerAddressCheck_" + delivery.getId();
        Class<? extends Job> checkJobClass = AddressCheckJob.class;
        LocalDateTime checkTriggerTime = delivery.getAddressDeadline().minusHours(1);
        scheduleJob(checkJobName, checkJobClass, checkTriggerTime, Map.of("deliveryId", delivery.getId()));
    }

}