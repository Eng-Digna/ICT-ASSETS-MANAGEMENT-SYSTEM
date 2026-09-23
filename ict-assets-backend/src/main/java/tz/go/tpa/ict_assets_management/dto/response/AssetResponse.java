package tz.go.tpa.ict_assets_management.dto.response;

import java.time.LocalDate;

public class AssetResponse {
    private Long id;
    private String assetType;
    /** Frontend-facing alias for assetType. */
    private String type;
    private String serialNumber;
    private String macAddress;
    private String brand;
    private String model;
    private String operatingSystem;
    private LocalDate warrantyStartDate;
    private LocalDate warrantyEndDate;
    private String status;
    private Long departmentId;
    private Long stationId;
    private String department;
    private String station;

    // getters/setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAssetType() {
        return assetType;
    }

    public void setAssetType(String assetType) {
        this.assetType = assetType;
    }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getOperatingSystem() {
        return operatingSystem;
    }

    public void setOperatingSystem(String operatingSystem) {
        this.operatingSystem = operatingSystem;
    }

    public LocalDate getWarrantyStartDate() {
        return warrantyStartDate;
    }

    public void setWarrantyStartDate(LocalDate d) {
        this.warrantyStartDate = d;
    }

    public LocalDate getWarrantyEndDate() {
        return warrantyEndDate;
    }

    public void setWarrantyEndDate(LocalDate d) {
        this.warrantyEndDate = d;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public Long getStationId() {
        return stationId;
    }

    public void setStationId(Long stationId) {
        this.stationId = stationId;
    }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public String getStation() { return station; }
    public void setStation(String station) { this.station = station; }
}
