package tz.go.tpa.ict_assets_management.dto.request;

import java.time.LocalDate;

public class UpdateAssetRequest {
    private String brand;
    private String model;
    private String operatingSystem;
    private LocalDate warrantyEndDate;

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

    public LocalDate getWarrantyEndDate() {
        return warrantyEndDate;
    }

    public void setWarrantyEndDate(LocalDate d) {
        this.warrantyEndDate = d;
    }
}
