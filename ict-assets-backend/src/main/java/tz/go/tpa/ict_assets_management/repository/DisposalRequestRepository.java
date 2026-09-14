package tz.go.tpa.ict_assets_management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import tz.go.tpa.ict_assets_management.entity.DisposalRequest;
import tz.go.tpa.ict_assets_management.enums.DisposalStatus;

public interface DisposalRequestRepository extends JpaRepository<DisposalRequest, Long>, JpaSpecificationExecutor<DisposalRequest> {
    boolean existsByAssetIdAndStatus(Long assetId, DisposalStatus status);
}