package tz.go.tpa.ict_assets_management.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class MaintenanceRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "asset_id", nullable = false)
    private Asset asset;

    @Column(nullable = false)
    private LocalDate serviceDate;

    @Column(nullable = false)
    private String description;

    @ManyToOne
    @JoinColumn(name = "logged_by", nullable = false)
    private User loggedBy;

    public MaintenanceRecord() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Asset getAsset() { return asset; }
    public void setAsset(Asset asset) { this.asset = asset; }
    public LocalDate getServiceDate() { return serviceDate; }
    public void setServiceDate(LocalDate serviceDate) { this.serviceDate = serviceDate; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public User getLoggedBy() { return loggedBy; }
    public void setLoggedBy(User loggedBy) { this.loggedBy = loggedBy; }
}