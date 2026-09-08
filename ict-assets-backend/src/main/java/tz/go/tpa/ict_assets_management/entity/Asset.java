package tz.go.tpa.ict_assets_management.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Asset {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String serialNumber;
    private String description;
    private String macAddress;
    private String model;

    public Asset() {
    }

    // getters/setters omitted for brevity
}
