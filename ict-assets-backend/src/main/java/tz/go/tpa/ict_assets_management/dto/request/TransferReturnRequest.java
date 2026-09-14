package tz.go.tpa.ict_assets_management.dto.request;

public class TransferReturnRequest {
    private String action;
    private Long newUserId;
    private Long newStationId;
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public Long getNewUserId() { return newUserId; }
    public void setNewUserId(Long newUserId) { this.newUserId = newUserId; }
    public Long getNewStationId() { return newStationId; }
    public void setNewStationId(Long newStationId) { this.newStationId = newStationId; }
}