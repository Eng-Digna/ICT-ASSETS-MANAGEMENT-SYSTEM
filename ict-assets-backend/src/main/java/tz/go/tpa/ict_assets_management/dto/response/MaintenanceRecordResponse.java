package tz.go.tpa.ict_assets_management.dto.response;

import java.time.LocalDate;

public class MaintenanceRecordResponse {
    private Long id;
    private Long assetId;
    private String assetSerialNumber;
    private String brand;
    private String model;
    private String assetStatus;
    private LocalDate serviceDate;
    private String description;
    private Long loggedBy;
    private String loggedByName;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getAssetId() { return assetId; }
    public void setAssetId(Long assetId) { this.assetId = assetId; }
    public String getAssetSerialNumber() { return assetSerialNumber; }
    public void setAssetSerialNumber(String assetSerialNumber) { this.assetSerialNumber = assetSerialNumber; }
    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public String getAssetStatus() { return assetStatus; }
    public void setAssetStatus(String assetStatus) { this.assetStatus = assetStatus; }
    public LocalDate getServiceDate() { return serviceDate; }
    public void setServiceDate(LocalDate serviceDate) { this.serviceDate = serviceDate; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Long getLoggedBy() { return loggedBy; }
    public void setLoggedBy(Long loggedBy) { this.loggedBy = loggedBy; }
    public String getLoggedByName() { return loggedByName; }
    public void setLoggedByName(String loggedByName) { this.loggedByName = loggedByName; }
}