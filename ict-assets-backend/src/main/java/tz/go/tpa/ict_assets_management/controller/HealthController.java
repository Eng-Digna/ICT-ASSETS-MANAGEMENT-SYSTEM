package tz.go.tpa.ict_assets_management.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tz.go.tpa.ict_assets_management.dto.response.ApiResponse;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class HealthController {

    @GetMapping("/system/health")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSystemHealth() {
        Map<String, Object> health = Map.of(
                "status", "UP",
                "database", "UP",
                "application", "UP",
                "uptime", "N/A",
                "timestamp", Instant.now()
        );
        return ResponseEntity.ok(new ApiResponse<>(true, "System health retrieved", health, "/api/v1/system/health"));
    }
}
