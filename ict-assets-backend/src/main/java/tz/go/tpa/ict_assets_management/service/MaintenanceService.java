package tz.go.tpa.ict_assets_management.service;

import tz.go.tpa.ict_assets_management.dto.request.LogMaintenanceRequest;
import tz.go.tpa.ict_assets_management.dto.request.MaintenanceSearchFilter;
import tz.go.tpa.ict_assets_management.dto.response.MaintenanceRecordResponse;
import tz.go.tpa.ict_assets_management.dto.response.PageResponse;

public interface MaintenanceService {
    MaintenanceRecordResponse logMaintenance(LogMaintenanceRequest request);
    MaintenanceRecordResponse completeMaintenance(Long id);
    PageResponse<MaintenanceRecordResponse> listRecords(MaintenanceSearchFilter filter);
    MaintenanceRecordResponse getRecord(Long id);
}