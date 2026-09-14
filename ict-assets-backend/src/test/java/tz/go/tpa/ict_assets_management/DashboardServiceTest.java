package tz.go.tpa.ict_assets_management;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import tz.go.tpa.ict_assets_management.controller.DashboardController;
import tz.go.tpa.ict_assets_management.dto.response.ApiResponse;
import tz.go.tpa.ict_assets_management.dto.response.DashboardOverviewResponse;
import tz.go.tpa.ict_assets_management.entity.Asset;
import tz.go.tpa.ict_assets_management.entity.Department;
import tz.go.tpa.ict_assets_management.entity.Station;
import tz.go.tpa.ict_assets_management.enums.AssetStatus;
import tz.go.tpa.ict_assets_management.enums.AssetType;
import tz.go.tpa.ict_assets_management.repository.DepartmentRepository;
import tz.go.tpa.ict_assets_management.repository.StationRepository;
import tz.go.tpa.ict_assets_management.entity.User;
import tz.go.tpa.ict_assets_management.repository.AssetRepository;
import tz.go.tpa.ict_assets_management.repository.UserRepository;
import tz.go.tpa.ict_assets_management.service.DashboardService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class DashboardServiceTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AssetRepository assetRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private DashboardService dashboardService;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        assetRepository.deleteAll();
        departmentRepository.deleteAll();
        stationRepository.deleteAll();

        userRepository.save(createUser("active@example.com", true));
        userRepository.save(createUser("inactive@example.com", false));
        Department department = new Department();
        department.setName("Dashboard Department");
        department = departmentRepository.save(department);
        Station station = new Station();
        station.setName("Dashboard Station");
        station = stationRepository.save(station);
        assetRepository.save(createAsset("DASH-001", department, station));
        assetRepository.save(createAsset("DASH-002", department, station));
    }

    @Test
    void returnsDashboardCountsAndSystemStatus() {
        DashboardOverviewResponse response = dashboardService.getOverview();

        assertEquals(2, response.getTotalUsers());
        assertEquals(1, response.getActiveUsers());
        assertEquals(1, response.getInactiveUsers());
        assertEquals(2, response.getTotalAssets());
        assertEquals("UP", response.getSystemStatus());
    }

    @Test
    void controllerReturnsSuccessfulApiResponse() {
        DashboardController controller = new DashboardController(dashboardService);

        ResponseEntity<ApiResponse<DashboardOverviewResponse>> response = controller.overview();

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(true, response.getBody().isSuccess());
        assertEquals("/api/v1/dashboard/overview", response.getBody().getPath());
        assertEquals(2, response.getBody().getData().getTotalUsers());
    }

    private User createUser(String email, boolean enabled) {
        User user = new User();
        user.setUsername(email);
        user.setEmail(email);
        user.setPasswordHash("password");
        user.setFirstName("Dashboard");
        user.setLastName("User");
        user.setEnabled(enabled);
        return user;
    }

    private Asset createAsset(String serialNumber, Department department, Station station) {
        Asset asset = new Asset();
        asset.setAssetType(AssetType.DESKTOP);
        asset.setSerialNumber(serialNumber);
        asset.setStatus(AssetStatus.REGISTERED);
        asset.setDepartment(department);
        asset.setStation(station);
        return asset;
    }
}
