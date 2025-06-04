package com.example.edutrack.accounts.service.interfaces;

import java.util.List;
import java.util.Map;

public interface SystemConfigService {
        
        Map<String, String> getSystemConfigs();
        
        void updateConfig(String key, String value);
        
        Map<String, Object> getSystemStatus();
}
