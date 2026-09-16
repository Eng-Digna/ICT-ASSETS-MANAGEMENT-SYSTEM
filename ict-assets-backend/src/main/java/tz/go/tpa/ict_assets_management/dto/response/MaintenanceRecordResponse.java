package tz.go.tpa.ict_assets_management.dto.response;

import java.time.LocalDate;

public class MaintenanceRecordResponse {
    private Long id;
    private Long assetId;
    private LocalDate serviceDate;
    private String description;
    private Long loggedBy;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getAssetId() { return assetId; }
    public void setAssetId(Long assetId) { this.assetId = assetId; }
    public LocalDate getServiceDate() { return serviceDate; }
    public void setServiceDate(LocalDate serviceDate) { this.serviceDate = serviceDate; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Long getLoggedBy() { return loggedBy; }
    public void setLoggedBy(Long loggedBy) { this.loggedBy = loggedBy; }
}