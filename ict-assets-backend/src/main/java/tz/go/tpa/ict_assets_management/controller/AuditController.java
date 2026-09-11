package tz.go.tpa.ict_assets_management.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tz.go.tpa.ict_assets_management.dto.request.AuditLogFilter;
import tz.go.tpa.ict_assets_management.dto.response.ApiResponse;
import tz.go.tpa.ict_assets_management.entity.AuditLog;
import tz.go.tpa.ict_assets_management.service.AuditService;

@RestController
@RequestMapping("/api/v1/audits")
public class AuditController {
    private final AuditService auditService;

    public AuditController(AuditService auditService) {
        this.auditService = auditService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or hasRole('AUDITOR')")
    public ResponseEntity<ApiResponse<Page<AuditLog>>> getAudits(@ModelAttribute AuditLogFilter filter, Pageable pageable) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Audit logs retrieved", auditService.getAudits(filter, pageable), "/api/v1/audits"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or hasRole('AUDITOR')")
    public ResponseEntity<ApiResponse<AuditLog>> getAudit(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Audit log retrieved", auditService.getById(id), "/api/v1/audits/" + id));
    }
}
