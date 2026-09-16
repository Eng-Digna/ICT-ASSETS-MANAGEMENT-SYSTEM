package tz.go.tpa.ict_assets_management;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import tz.go.tpa.ict_assets_management.dto.request.AuditLogFilter;
import tz.go.tpa.ict_assets_management.entity.AuditLog;
import tz.go.tpa.ict_assets_management.exception.ResourceNotFoundException;
import tz.go.tpa.ict_assets_management.repository.AuditLogRepository;
import tz.go.tpa.ict_assets_management.service.AuditService;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class AuditServiceTest {
    private static final Instant FIRST_TIMESTAMP = Instant.parse("2026-01-01T10:00:00Z");
    private static final Instant SECOND_TIMESTAMP = Instant.parse("2026-01-02T10:00:00Z");
    private static final Instant THIRD_TIMESTAMP = Instant.parse("2026-01-03T10:00:00Z");

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private AuditService auditService;

    @BeforeEach
    void setUp() {
        auditLogRepository.deleteAll();
        auditLogRepository.save(createLog(10L, "alice", "CREATE", "ASSET", "A-1", true, FIRST_TIMESTAMP));
        auditLogRepository.save(createLog(20L, "bob", "UPDATE", "ASSET", "A-2", false, SECOND_TIMESTAMP));
        auditLogRepository.save(createLog(10L, "alice", "UPDATE", "USER", "U-1", true, THIRD_TIMESTAMP));
    }

    @Test
    void returnsUnfilteredPaginatedResultsInStableOrder() {
        Page<AuditLog> result = auditService.getAudits(new AuditLogFilter(), PageRequest.of(0, 2));

        assertEquals(3, result.getTotalElements());
        assertEquals(2, result.getContent().size());
        assertEquals(THIRD_TIMESTAMP, result.getContent().get(0).getTimestamp());
    }

    @Test
    void filtersByUserId() {
        AuditLogFilter filter = new AuditLogFilter();
        filter.setUserId(10L);

        assertEquals(2, auditService.getAudits(filter, PageRequest.of(0, 20)).getTotalElements());
    }

    @Test
    void filtersByAction() {
        AuditLogFilter filter = new AuditLogFilter();
        filter.setAction("upd");

        assertEquals(2, auditService.getAudits(filter, PageRequest.of(0, 20)).getTotalElements());
    }

    @Test
    void filtersByResource() {
        AuditLogFilter filter = new AuditLogFilter();
        filter.setResourceType("ASSET");
        filter.setResourceId("A-1");

        Page<AuditLog> result = auditService.getAudits(filter, PageRequest.of(0, 20));

        assertEquals(1, result.getTotalElements());
        assertEquals("A-1", result.getContent().get(0).getResourceId());
    }

    @Test
    void filtersBySuccessOrFailure() {
        AuditLogFilter filter = new AuditLogFilter();
        filter.setSuccess(false);

        Page<AuditLog> result = auditService.getAudits(filter, PageRequest.of(0, 20));

        assertEquals(1, result.getTotalElements());
        assertEquals("bob", result.getContent().get(0).getUsername());
    }

    @Test
    void filtersByDateRange() {
        AuditLogFilter filter = new AuditLogFilter();
        filter.setStart(SECOND_TIMESTAMP);
        filter.setEnd(THIRD_TIMESTAMP);

        assertEquals(2, auditService.getAudits(filter, PageRequest.of(0, 20)).getTotalElements());
    }

    @Test
    void combinesMultipleFiltersWithAndLogic() {
        AuditLogFilter filter = new AuditLogFilter();
        filter.setUserId(10L);
        filter.setAction("UPDATE");
        filter.setSuccess(true);

        Page<AuditLog> result = auditService.getAudits(filter, PageRequest.of(0, 20));

        assertEquals(1, result.getTotalElements());
        assertEquals("U-1", result.getContent().get(0).getResourceId());
    }

    @Test
    void rejectsInvalidDateRange() {
        AuditLogFilter filter = new AuditLogFilter();
        filter.setStart(THIRD_TIMESTAMP);
        filter.setEnd(SECOND_TIMESTAMP);

        assertThrows(IllegalArgumentException.class,
                () -> auditService.getAudits(filter, PageRequest.of(0, 20)));
    }

    @Test
    void throwsResourceNotFoundForMissingAudit() {
        assertThrows(ResourceNotFoundException.class, () -> auditService.getById(999999L));
    }

    private AuditLog createLog(Long userId, String username, String action, String resourceType,
                               String resourceId, boolean success, Instant timestamp) {
        AuditLog auditLog = new AuditLog();
        auditLog.setUserId(userId);
        auditLog.setUsername(username);
        auditLog.setAction(action);
        auditLog.setResourceType(resourceType);
        auditLog.setResourceId(resourceId);
        auditLog.setSuccess(success);
        auditLog.setTimestamp(timestamp);
        return auditLog;
    }
}
