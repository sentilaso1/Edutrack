package com.example.edutrack.accounts.service.implementations;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.edutrack.accounts.repository.PropertyRepository;
import com.example.edutrack.accounts.model.Property;
import com.example.edutrack.accounts.model.RequestLog;
import com.example.edutrack.accounts.repository.RequestLogRepository;
import com.example.edutrack.accounts.service.interfaces.SystemConfigService;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HWDiskStore;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;

@Service
public class SystemConfigServiceImpl implements SystemConfigService {
    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private RequestLogRepository requestLogRepository;

    private final SystemInfo systemInfo = new SystemInfo();

    @Override
    public String getValue(String key) {
        return propertyRepository.findByKey(key)
                .map(Property::getValue)
                .orElse("");
    }

    @Override
    public void updateValue(String key, String value) {
        Property p = propertyRepository.findByKey(key).orElse(new Property());
        p.setKey(key);
        p.setValue(value);
        propertyRepository.save(p);
    }

    @Override
    public Map<String, String> getConfigs(String... keys) {
        Map<String, String> map = new HashMap<>();
        for (String key : keys) {
            map.put(key, getValue(key));
        }
        return map;
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
            status.put("error", "Unable to retrieve system status");
        }
        return status;
    }

    private String formatBytes(long bytes) {
        if (bytes < 1024)
            return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp - 1) + "";
        return String.format("%.1f %sB", bytes / Math.pow(1024, exp), pre);
    }
    
    @Override
    public void importFromCsv(MultipartFile file) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            boolean isFirst = true;
            while ((line = reader.readLine()) != null) {
                if (isFirst) { isFirst = false; continue; } // Skip header
                String[] tokens = line.split(",");
                if (tokens.length < 6) continue;

                RequestLog log = new RequestLog();
                log.setTimestamp(LocalDateTime.parse(tokens[1].trim()));
                log.setIp(tokens[2].trim());
                log.setMethod(tokens[3].trim());
                log.setUri(tokens[4].trim());
                log.setParameters(tokens[5].trim());

                requestLogRepository.save(log);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to import log file", e);
        }
    }

    @Override
    public void exportToCsv(Writer writer) {
        List<RequestLog> logs = requestLogRepository.findAll();
        try (BufferedWriter bw = new BufferedWriter(writer)) {
            bw.write("ID,Timestamp,IP,Method,URI,Parameters\n");
            for (RequestLog log : logs) {
                bw.write(String.format("%d,%s,%s,%s,%s,%s\n",
                    log.getId(),
                    log.getTimestamp(),
                    log.getIp(),
                    log.getMethod(),
                    log.getUri(),
                    log.getParameters().replace(",", " ")  // avoid CSV corruption
                ));
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to export logs", e);
        }
    }

    @Override
    public void clearAllLogs() {
        requestLogRepository.deleteAll();
    }

}
