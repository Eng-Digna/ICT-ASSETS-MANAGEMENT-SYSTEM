package tz.go.tpa.ict_assets_management.dto.request;

public class TransferReturnRequest {
    private String action;
    private String assigneeName;
    private Long newStationId;
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public String getAssigneeName() { return assigneeName; }
    public void setAssigneeName(String assigneeName) { this.assigneeName = assigneeName; }
    public Long getNewStationId() { return newStationId; }
    public void setNewStationId(Long newStationId) { this.newStationId = newStationId; }
}