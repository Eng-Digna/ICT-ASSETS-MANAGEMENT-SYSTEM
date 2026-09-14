package tz.go.tpa.ict_assets_management.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class SubmitDisposalRequest {
    @NotNull private Long assetId;
    @NotBlank private String reason;
    public Long getAssetId() { return assetId; }
    public void setAssetId(Long assetId) { this.assetId = assetId; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}