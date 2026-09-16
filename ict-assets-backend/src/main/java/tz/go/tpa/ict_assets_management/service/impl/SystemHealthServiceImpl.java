package tz.go.tpa.ict_assets_management.service.impl;

import org.springframework.stereotype.Service;
import tz.go.tpa.ict_assets_management.service.SystemHealthService;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class SystemHealthServiceImpl implements SystemHealthService {
    @Override
    public Map<String, Object> getHealthSummary() {
        Map<String, Object> health = new LinkedHashMap<>();
        health.put("status", "UP");
        health.put("database", "UP");
        health.put("application", "UP");
        health.put("timestamp", Instant.now());
        return health;
    }
}
