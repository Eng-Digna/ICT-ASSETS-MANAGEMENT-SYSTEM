package tz.go.tpa.ict_assets_management.dto.request;

import jakarta.validation.constraints.NotNull;

public class AssignAssetRequest {
    @NotNull private Long assetId;
    @NotNull private String assigneeName;
    @NotNull private Long stationId;
    public Long getAssetId() { return assetId; }
    public void setAssetId(Long assetId) { this.assetId = assetId; }
    public String getAssigneeName() { return assigneeName; }
    public void setAssigneeName(String assigneeName) { this.assigneeName = assigneeName; }
    public Long getStationId() { return stationId; }
    public void setStationId(Long stationId) { this.stationId = stationId; }
}