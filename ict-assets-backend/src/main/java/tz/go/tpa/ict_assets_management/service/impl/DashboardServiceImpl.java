package tz.go.tpa.ict_assets_management.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import tz.go.tpa.ict_assets_management.entity.User;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import tz.go.tpa.ict_assets_management.entity.Asset;
import java.util.ArrayList;
import java.util.List;
import tz.go.tpa.ict_assets_management.dto.response.DashboardOverviewResponse;
import tz.go.tpa.ict_assets_management.enums.AssetStatus;
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
        Specification<Asset> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.notEqual(root.get("status"), AssetStatus.DISPOSED));

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getPrincipal() instanceof User) {
                User user = (User) auth.getPrincipal();
                boolean isRegistrar = user.getRoles().stream().anyMatch(r -> r.getName().name().equals("REGISTRAR"));
                boolean isAdmin = user.getRoles().stream().anyMatch(r -> r.getName().name().equals("ADMINISTRATOR"));
                if (isRegistrar && !isAdmin && user.getStation() != null) {
                    predicates.add(cb.equal(root.get("station").get("id"), user.getStation().getId()));
                }
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        long assetCount = assetRepository.count(spec);

        Map<String, Object> healthSummary = systemHealthService.getHealthSummary();
        return new DashboardOverviewResponse(
                userRepository.count(),
                userRepository.countByEnabledTrue(),
                userRepository.countByEnabledFalse(),
                assetCount,
                String.valueOf(healthSummary.get("status"))
        );
    }
}
