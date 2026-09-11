package tz.go.tpa.ict_assets_management.service.impl;

import org.springframework.stereotype.Service;
import tz.go.tpa.ict_assets_management.dto.response.DashboardOverviewResponse;
import tz.go.tpa.ict_assets_management.repository.AssetRepository;
import tz.go.tpa.ict_assets_management.repository.UserRepository;
import tz.go.tpa.ict_assets_management.service.DashboardService;
import tz.go.tpa.ict_assets_management.service.SystemHealthService;

import java.util.Map;

@Service
public class DashboardServiceImpl implements DashboardService {
    private final UserRepository userRepository;
    private final AssetRepository assetRepository;
    private final SystemHealthService systemHealthService;

    public DashboardServiceImpl(UserRepository userRepository, AssetRepository assetRepository,
                                SystemHealthService systemHealthService) {
        this.userRepository = userRepository;
        this.assetRepository = assetRepository;
        this.systemHealthService = systemHealthService;
    }

    @Override
    public DashboardOverviewResponse getOverview() {
        Map<String, Object> healthSummary = systemHealthService.getHealthSummary();
        return new DashboardOverviewResponse(
                userRepository.count(),
                userRepository.countByEnabledTrue(),
                userRepository.countByEnabledFalse(),
                assetRepository.count(),
                String.valueOf(healthSummary.get("status"))
        );
    }
}
