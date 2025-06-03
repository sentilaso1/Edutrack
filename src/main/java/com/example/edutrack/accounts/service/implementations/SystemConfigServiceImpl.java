package com.example.edutrack.accounts.service.implementations;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.edutrack.accounts.repository.PropertyRepository;
import com.example.edutrack.accounts.model.Property;
import com.example.edutrack.accounts.service.interfaces.SystemConfigService;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HWDiskStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SystemConfigServiceImpl implements SystemConfigService {
    private static final Logger logger = LoggerFactory.getLogger(SystemConfigService.class);
    @Autowired
    private PropertyRepository propertyRepository;

    private final SystemInfo systemInfo = new SystemInfo();

    public Map<String, String> getSystemConfigs() {
        List<Property> properties = propertyRepository.findAll();
        Map<String, String> configs = new HashMap<>();
        for (Property prop : properties) {
            configs.put(prop.getKey(), prop.getValue());
        }
        return configs;
    }

    @Transactional
    public void updateConfig(String key, String value) {
        Property property = propertyRepository.findByKey(key);
        if (property == null) {
            property = new Property();
            property.setKey(key);
            property.setCreatedDate(new Date());
        }
        property.setValue(value);
        property.setUpdatedDate(new Date());
        propertyRepository.save(property);
        logger.info("Updated config: {} = {}", key, value);
    }

    public Map<String, Object> getSystemStatus() {
        Map<String, Object> status = new HashMap<>();
        try {
            // CPU Usage
            CentralProcessor processor = systemInfo.getHardware().getProcessor();
            long[] prevTicks = processor.getSystemCpuLoadTicks();
            try { Thread.sleep(1000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            double cpuLoad = processor.getSystemCpuLoadBetweenTicks(prevTicks) * 100;
            status.put("cpuUsage", String.format("%.2f%%", cpuLoad));

            // Memory Usage
            GlobalMemory memory = systemInfo.getHardware().getMemory();
            long totalMemory = memory.getTotal();
            long availableMemory = memory.getAvailable();
            double memoryUsage = ((double) (totalMemory - availableMemory) / totalMemory) * 100;
            status.put("memoryUsage", String.format("%.2f%%", memoryUsage));
            status.put("totalMemory", formatBytes(totalMemory));
            status.put("availableMemory", formatBytes(availableMemory));

            // Disk Usage
            long totalDisk = 0;
            long usedDisk = 0;
            for (HWDiskStore disk : systemInfo.getHardware().getDiskStores()) {
                totalDisk += disk.getSize();
                usedDisk += disk.getSize() - disk.getWriteBytes();
            }
            double diskUsage = totalDisk > 0 ? ((double) usedDisk / totalDisk) * 100 : 0;
            status.put("diskUsage", String.format("%.2f%%", diskUsage));
            status.put("totalDisk", formatBytes(totalDisk));
            status.put("usedDisk", formatBytes(usedDisk));
        } catch (Exception e) {
            logger.error("Error retrieving system status: {}", e.getMessage());
            status.put("error", "Unable to retrieve system status");
        }
        return status;
    }

    private String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp-1) + "";
        return String.format("%.1f %sB", bytes / Math.pow(1024, exp), pre);
    }
}
