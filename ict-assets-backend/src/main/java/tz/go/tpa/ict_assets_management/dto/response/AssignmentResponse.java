package tz.go.tpa.ict_assets_management.dto.response;

import java.time.LocalDate;

public class AssignmentResponse {
    private Long id;
    private Long assetId;
    private String assetSerialNumber;
    private String assetType;
    private String assigneeName;
    private Long stationId;
    private String stationName;
    private String departmentName;
    private LocalDate assignedDate;
    private LocalDate returnedDate;
    private String status;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getAssetId() { return assetId; }
    public void setAssetId(Long assetId) { this.assetId = assetId; }
    public String getAssetSerialNumber() { return assetSerialNumber; }
    public void setAssetSerialNumber(String assetSerialNumber) { this.assetSerialNumber = assetSerialNumber; }
    public String getAssetType() { return assetType; }
    public void setAssetType(String assetType) { this.assetType = assetType; }
    public String getAssigneeName() { return assigneeName; }
    public void setAssigneeName(String assigneeName) { this.assigneeName = assigneeName; }
    public Long getStationId() { return stationId; }
    public void setStationId(Long stationId) { this.stationId = stationId; }
    public String getStationName() { return stationName; }
    public void setStationName(String stationName) { this.stationName = stationName; }
    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }
    public LocalDate getAssignedDate() { return assignedDate; }
    public void setAssignedDate(LocalDate assignedDate) { this.assignedDate = assignedDate; }
    public LocalDate getReturnedDate() { return returnedDate; }
    public void setReturnedDate(LocalDate returnedDate) { this.returnedDate = returnedDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}