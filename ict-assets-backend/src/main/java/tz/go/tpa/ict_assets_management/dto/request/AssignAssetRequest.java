package tz.go.tpa.ict_assets_management.dto.request;

import jakarta.validation.constraints.NotNull;

public class AssignAssetRequest {
    @NotNull private Long assetId;
    @NotNull private Long userId;
    @NotNull private Long stationId;
    public Long getAssetId() { return assetId; }
    public void setAssetId(Long assetId) { this.assetId = assetId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getStationId() { return stationId; }
    public void setStationId(Long stationId) { this.stationId = stationId; }
}