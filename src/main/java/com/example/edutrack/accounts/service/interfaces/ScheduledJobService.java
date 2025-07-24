package com.example.edutrack.accounts.service.interfaces;

import com.example.edutrack.accounts.dto.ScheduledJobDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.example.edutrack.accounts.dto.JobStats;

public interface ScheduledJobService {
        Page<ScheduledJobDTO> getJobs(String search, Pageable pageable);
        ScheduledJobDTO getJob(Long id);
        void updateJob(Long id, ScheduledJobDTO dto);
        void toggleJob(Long id, boolean active);
        void runJobNow(Long id);
        JobStats getJobSummary();
        void createJob(ScheduledJobDTO dto);
        boolean isJobRunning(Long id);
        void deleteJob(Long id);
}
