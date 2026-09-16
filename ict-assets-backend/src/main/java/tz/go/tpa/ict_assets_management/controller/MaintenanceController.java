package tz.go.tpa.ict_assets_management.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tz.go.tpa.ict_assets_management.dto.request.LogMaintenanceRequest;
import tz.go.tpa.ict_assets_management.dto.request.MaintenanceSearchFilter;
import tz.go.tpa.ict_assets_management.dto.response.MaintenanceRecordResponse;
import tz.go.tpa.ict_assets_management.dto.response.PageResponse;
import tz.go.tpa.ict_assets_management.service.MaintenanceService;

@RestController
@RequestMapping("/api/v1/maintenance-records")
public class MaintenanceController {
    private final MaintenanceService maintenanceService;

    public MaintenanceController(MaintenanceService maintenanceService) {
        this.maintenanceService = maintenanceService;
    }

    @PostMapping
    @PreAuthorize("hasRole('REGISTRAR') or hasRole('ADMINISTRATOR')")
    public ResponseEntity<MaintenanceRecordResponse> logMaintenance(@Valid @RequestBody LogMaintenanceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(maintenanceService.logMaintenance(request));
    }

    @GetMapping
    public ResponseEntity<PageResponse<MaintenanceRecordResponse>> listRecords(
            @RequestParam(required = false) Long assetId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        MaintenanceSearchFilter filter = new MaintenanceSearchFilter();
        filter.setAssetId(assetId);
        filter.setPage(page);
        filter.setPageSize(pageSize);
        return ResponseEntity.ok(maintenanceService.listRecords(filter));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MaintenanceRecordResponse> getRecord(@PathVariable Long id) {
        return ResponseEntity.ok(maintenanceService.getRecord(id));
    }

    @PatchMapping("/{id}/complete")
    @PreAuthorize("hasRole('REGISTRAR') or hasRole('ADMINISTRATOR')")
    public ResponseEntity<MaintenanceRecordResponse> completeMaintenance(@PathVariable Long id) {
        return ResponseEntity.ok(maintenanceService.completeMaintenance(id));
    }
}