package tz.go.tpa.ict_assets_management.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tz.go.tpa.ict_assets_management.dto.response.ApiResponse;
import tz.go.tpa.ict_assets_management.dto.response.DashboardOverviewResponse;
import tz.go.tpa.ict_assets_management.service.DashboardService;

@RestController
@RequestMapping("/api/v1/dashboard")
public class DashboardController {
    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/overview")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<DashboardOverviewResponse>> overview() {
        return ResponseEntity.ok(new ApiResponse<>(true, "Dashboard overview retrieved",
                dashboardService.getOverview(), "/api/v1/dashboard/overview"));
    }
}
