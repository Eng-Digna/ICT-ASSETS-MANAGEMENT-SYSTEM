package tz.go.tpa.ict_assets_management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import tz.go.tpa.ict_assets_management.entity.Assignment;
import tz.go.tpa.ict_assets_management.enums.AssignmentStatus;

public interface AssignmentRepository extends JpaRepository<Assignment, Long>, JpaSpecificationExecutor<Assignment> {
	boolean existsByAssetIdAndStatus(Long assetId, AssignmentStatus status);
}