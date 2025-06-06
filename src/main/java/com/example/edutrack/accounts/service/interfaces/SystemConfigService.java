package com.example.edutrack.accounts.service.interfaces;

import java.io.Writer;
import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

public interface SystemConfigService {
        
        Map<String, String> getSystemConfigs();
        
        void updateConfig(String key, String value);
        
        Map<String, Object> getSystemStatus();

        void clearAllLogs();

        void importFromCsv(MultipartFile file);

        void exportToCsv(Writer writer);
}
