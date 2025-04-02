package com.example.demo.controller.admin;

import com.example.demo.base.ApiResponse;
import com.example.demo.domain.dto.Scheduler.SchedulerResponseDTO;
import com.example.demo.service.general.SchedulerService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static com.example.demo.base.status.SuccessStatus._OK;

@RestController
@RequestMapping("/api/permit/scheduler")
@RequiredArgsConstructor
public class SchedulerController {

    private final SchedulerService schedulerService;

    @Operation(summary = "스케줄링 조회")
    @GetMapping("")
    public ApiResponse<SchedulerResponseDTO> getJobKeys() {
        return ApiResponse.of(_OK, schedulerService.getJobKeys());
    }

    @Operation(summary = "스케줄러 설정")
    @PostMapping("")
    public ApiResponse<?> scheduleAll() {
        schedulerService.scheduleAll();

        return ApiResponse.of(_OK, null);
    }

    @Operation(summary = "새 래플 스케줄링")
    @PostMapping("/new/{raffleId}")
    public ApiResponse<?> scheduleNew(@PathVariable Long raffleId) {
        schedulerService.scheduleNew(raffleId);

        return ApiResponse.of(_OK, null);
    }

}
