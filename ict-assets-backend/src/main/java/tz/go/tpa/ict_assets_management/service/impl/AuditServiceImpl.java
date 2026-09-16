package tz.go.tpa.ict_assets_management.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import tz.go.tpa.ict_assets_management.dto.request.AuditLogFilter;
import tz.go.tpa.ict_assets_management.entity.AuditLog;
import tz.go.tpa.ict_assets_management.exception.ResourceNotFoundException;
import tz.go.tpa.ict_assets_management.repository.AuditLogRepository;
import tz.go.tpa.ict_assets_management.service.AuditService;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

@Service
public class AuditServiceImpl implements AuditService {
    private static final int MAX_PAGE_SIZE = 100;
    private static final Sort DEFAULT_SORT = Sort.by(
            Sort.Order.desc("timestamp"),
            Sort.Order.desc("id"));

    private final AuditLogRepository auditLogRepository;

    public AuditServiceImpl(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Override
    public Page<AuditLog> getAudits(AuditLogFilter filter, Pageable pageable) {
        validateDateRange(filter);
        return auditLogRepository.findAll(toSpecification(filter), normalizePageable(pageable));
    }

    @Override
    public AuditLog getById(Long id) {
        return auditLogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Audit log not found with id: " + id));
    }

    private Pageable normalizePageable(Pageable pageable) {
        Sort sort = pageable.getSort().isSorted() ? pageable.getSort() : DEFAULT_SORT;
        int pageSize = Math.min(pageable.getPageSize(), MAX_PAGE_SIZE);
        return PageRequest.of(pageable.getPageNumber(), pageSize, sort);
    }

    private void validateDateRange(AuditLogFilter filter) {
        if (filter != null && filter.getStart() != null && filter.getEnd() != null
                && !filter.getStart().isBefore(filter.getEnd())) {
            throw new IllegalArgumentException("start must be before end");
        }
    }

    private Specification<AuditLog> toSpecification(AuditLogFilter filter) {
        return (root, query, criteriaBuilder) -> {
            if (filter == null) {
                return criteriaBuilder.conjunction();
            }

            List<Predicate> predicates = new ArrayList<>();
            addEqualPredicate(predicates, criteriaBuilder, root.get("userId"), filter.getUserId());
            addEqualPredicate(predicates, criteriaBuilder, root.get("username"), filter.getUsername());
            addContainsPredicate(predicates, criteriaBuilder, root.get("action"), filter.getAction());
            addEqualPredicate(predicates, criteriaBuilder, root.get("resourceType"), filter.getResourceType());
            addEqualPredicate(predicates, criteriaBuilder, root.get("resourceId"), filter.getResourceId());
            addEqualPredicate(predicates, criteriaBuilder, root.get("httpMethod"), filter.getHttpMethod());
            addEqualPredicate(predicates, criteriaBuilder, root.get("endpoint"), filter.getEndpoint());
            addEqualPredicate(predicates, criteriaBuilder, root.get("success"), filter.getSuccess());
            addEqualPredicate(predicates, criteriaBuilder, root.get("ipAddress"), filter.getIpAddress());
            addEqualPredicate(predicates, criteriaBuilder, root.get("correlationId"), filter.getCorrelationId());
            addEqualPredicate(predicates, criteriaBuilder, root.get("requestId"), filter.getRequestId());

            if (filter.getStart() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("timestamp"), filter.getStart()));
            }
            if (filter.getEnd() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("timestamp"), filter.getEnd()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private void addEqualPredicate(List<Predicate> predicates, jakarta.persistence.criteria.CriteriaBuilder criteriaBuilder,
                                   jakarta.persistence.criteria.Path<?> path, Object value) {
        if (value != null && (!(value instanceof String string) || !string.isBlank())) {
            predicates.add(criteriaBuilder.equal(path, value));
        }
    }

    private void addContainsPredicate(List<Predicate> predicates, jakarta.persistence.criteria.CriteriaBuilder criteriaBuilder,
                                      jakarta.persistence.criteria.Path<String> path, String value) {
        if (value != null && !value.isBlank()) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(path), "%" + value.toLowerCase() + "%"));
        }
    }
}
