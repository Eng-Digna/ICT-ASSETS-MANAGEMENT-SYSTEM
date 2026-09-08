package tz.go.tpa.ict_assets_management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tz.go.tpa.ict_assets_management.entity.Asset;

public interface AssetRepository extends JpaRepository<Asset, Long> {
    // Add query methods as needed
}
