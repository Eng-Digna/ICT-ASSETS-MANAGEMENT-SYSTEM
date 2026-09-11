package tz.go.tpa.ict_assets_management.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.JpaRepository;
import tz.go.tpa.ict_assets_management.entity.AuditLog;

import java.time.Instant;
import java.util.List;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long>, JpaSpecificationExecutor<AuditLog> {
    Page<AuditLog> findByUserId(Long userId, Pageable pageable);

    Page<AuditLog> findByActionContainingIgnoreCase(String action, Pageable pageable);

    Page<AuditLog> findByResourceTypeAndResourceId(String resourceType, String resourceId, Pageable pageable);

    List<AuditLog> findByTimestampBetween(Instant start, Instant end);
}
