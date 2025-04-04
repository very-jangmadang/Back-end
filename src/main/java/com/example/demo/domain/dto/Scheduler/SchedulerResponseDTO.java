package com.example.demo.domain.dto.Scheduler;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.quartz.JobKey;

import java.util.Set;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SchedulerResponseDTO {
    private Set<JobKey> JobKeys;
}
