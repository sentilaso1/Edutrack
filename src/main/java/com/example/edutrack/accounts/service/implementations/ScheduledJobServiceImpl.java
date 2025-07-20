package com.example.edutrack.accounts.service.implementations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.example.edutrack.accounts.dto.ScheduledJobDTO;
import com.example.edutrack.accounts.model.ScheduledJob;
import com.example.edutrack.accounts.repository.ScheduledJobRepository;
import com.example.edutrack.accounts.service.interfaces.ScheduledJobService;
import com.example.edutrack.accounts.dto.JobStats;
import org.springframework.beans.BeanUtils;

@Service
public class ScheduledJobServiceImpl implements ScheduledJobService {
        @Autowired
        private ScheduledJobRepository jobRepo;

        @Override
        public Page<ScheduledJobDTO> getJobs(String search, Pageable pageable) {
                Page<ScheduledJob> page = jobRepo.findByNameContainingIgnoreCase(search == null ? "" : search,
                                pageable);
                return page.map(this::mapToDTO);
        }

        @Override
        public ScheduledJobDTO getJob(Long id) {
                return jobRepo.findById(id).map(this::mapToDTO).orElseThrow();
        }

        @Override
        public void updateJob(Long id, ScheduledJobDTO dto) {
                ScheduledJob job = jobRepo.findById(id).orElseThrow();
                job.setCronExpression(dto.getCronExpression());
                jobRepo.save(job);
        }

        @Override
        public void toggleJob(Long id, boolean active) {
                ScheduledJob job = jobRepo.findById(id).orElseThrow();
                job.setActive(active);
                jobRepo.save(job);
        }

        @Override
        public void runJobNow(Long id) {
                ScheduledJob job = jobRepo.findById(id).orElseThrow();
                if (!job.isActive()) {
                        throw new IllegalStateException("Job is not active");
                }
                job.setLastRunTime(java.time.LocalDateTime.now());
                jobRepo.save(job);
        }

        private ScheduledJobDTO mapToDTO(ScheduledJob job) {
                ScheduledJobDTO dto = new ScheduledJobDTO();
                BeanUtils.copyProperties(job, dto);
                return dto;
        }

        public JobStats getJobSummary() {
                JobStats stats = new JobStats();
                stats.setTotal((int) jobRepo.count());
                stats.setActiveCount(jobRepo.countByActive(true));
                stats.setInactiveCount(jobRepo.countByActive(false));
                return stats;
        }
}
