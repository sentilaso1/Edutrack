package com.example.edutrack.accounts.service.interfaces;

import java.io.Writer;
import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

public interface SystemConfigService {
        
        String getValue(String key);

        void updateValue(String key, String value);

        Map<String, String> getConfigs(String... keys);
        
        Map<String, Object> getSystemStatus();

        void clearAllLogs();

        void importFromCsv(MultipartFile file);

        void exportToCsv(Writer writer);
}
