package tz.go.tpa.ict_assets_management.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import tz.go.tpa.ict_assets_management.dto.request.AuditLogFilter;
import tz.go.tpa.ict_assets_management.entity.AuditLog;

public interface AuditService {
    Page<AuditLog> getAudits(AuditLogFilter filter, Pageable pageable);

    AuditLog getById(Long id);
}
