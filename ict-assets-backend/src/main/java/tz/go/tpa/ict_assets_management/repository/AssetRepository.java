package tz.go.tpa.ict_assets_management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import tz.go.tpa.ict_assets_management.entity.Asset;

public interface AssetRepository extends JpaRepository<Asset, Long>, JpaSpecificationExecutor<Asset> {
    boolean existsBySerialNumber(String serialNumber);
}