package com.example.edutrack.accounts.service.interfaces;

import java.time.LocalDateTime;
import java.util.List;
import com.example.edutrack.accounts.dto.RevenueChartDTO;
import com.example.edutrack.accounts.dto.ManagerStatsDTO;
import com.example.edutrack.accounts.dto.TopMentorDTO;

public interface ManagerStatsService {
        public ManagerStatsDTO getManagerStats(String period);
        public List<RevenueChartDTO> getRevenueChartData(String period, LocalDateTime startDate);
        public List<TopMentorDTO> getTopMentors(LocalDateTime startDate);
}
