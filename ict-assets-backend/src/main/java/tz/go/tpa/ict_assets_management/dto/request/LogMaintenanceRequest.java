package tz.go.tpa.ict_assets_management.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class LogMaintenanceRequest {
    @NotNull private Long assetId;
    @NotNull private LocalDate serviceDate;
    @NotBlank private String description;

    public Long getAssetId() { return assetId; }
    public void setAssetId(Long assetId) { this.assetId = assetId; }
    public LocalDate getServiceDate() { return serviceDate; }
    public void setServiceDate(LocalDate serviceDate) { this.serviceDate = serviceDate; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}