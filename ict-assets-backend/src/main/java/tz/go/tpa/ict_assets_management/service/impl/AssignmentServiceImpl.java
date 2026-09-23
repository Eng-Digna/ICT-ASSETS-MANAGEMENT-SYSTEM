package tz.go.tpa.ict_assets_management.service.impl;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tz.go.tpa.ict_assets_management.dto.request.*;
import tz.go.tpa.ict_assets_management.dto.response.*;
import tz.go.tpa.ict_assets_management.entity.*;
import tz.go.tpa.ict_assets_management.enums.*;
import tz.go.tpa.ict_assets_management.exception.*;
import tz.go.tpa.ict_assets_management.repository.*;
import tz.go.tpa.ict_assets_management.service.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class AssignmentServiceImpl implements AssignmentService {
    private final AssignmentRepository assignmentRepository;
    private final AssetService assetService;
    private final StationRepository stationRepository;

    public AssignmentServiceImpl(AssignmentRepository assignmentRepository, AssetService assetService,
                                  StationRepository stationRepository) {
        this.assignmentRepository = assignmentRepository;
        this.assetService = assetService;
        this.stationRepository = stationRepository;
    }

    @Override @Transactional
    public AssignmentResponse assignAsset(AssignAssetRequest request) {
        Asset asset = assetService.getAssetEntityOrThrow(request.getAssetId());
        if (asset.getStatus() != AssetStatus.REGISTERED
                || assignmentRepository.existsByAssetIdAndStatus(asset.getId(), AssignmentStatus.ACTIVE)) {
            throw new DuplicateResourceException("Asset is not available for assignment");
        }
        String assigneeName = request.getAssigneeName();
        Station station = stationRepository.findById(request.getStationId()).orElseThrow(() -> new ResourceNotFoundException("No station with ID " + request.getStationId()));
        Assignment assignment = new Assignment();
        assignment.setAsset(asset); assignment.setAssigneeName(assigneeName); assignment.setStation(station);
        assignment.setAssignedDate(LocalDate.now()); assignment.setStatus(AssignmentStatus.ACTIVE);
        assetService.updateAssetStatus(asset.getId(), AssetStatus.ASSIGNED);
        return toResponse(assignmentRepository.save(assignment));
    }

    @Override @Transactional(readOnly = true)
    public PageResponse<AssignmentResponse> listAssignments(AssignmentSearchFilter filter) {
        Specification<Assignment> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (filter.getAssetId() != null) predicates.add(cb.equal(root.get("asset").get("id"), filter.getAssetId()));
            if (filter.getAssigneeName() != null && !filter.getAssigneeName().isEmpty()) predicates.add(cb.like(cb.lower(root.get("assigneeName")), "%" + filter.getAssigneeName().toLowerCase() + "%"));
            if (filter.getStatus() != null) predicates.add(cb.equal(root.get("status"), AssignmentStatus.valueOf(filter.getStatus().toUpperCase())));
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        Page<Assignment> result = assignmentRepository.findAll(spec, PageRequest.of(filter.getPage(), filter.getPageSize()));
        return new PageResponse<>(result.getContent().stream().map(this::toResponse).toList(), filter.getPage() + 1, filter.getPageSize(), result.getTotalElements());
    }

    @Override @Transactional(readOnly = true)
    public AssignmentResponse getAssignment(Long id) { return toResponse(getAssignmentOrThrow(id)); }

    @Override @Transactional
    public AssignmentResponse transferOrReturn(Long id, TransferReturnRequest request) {
        Assignment assignment = getAssignmentOrThrow(id);
        if ("return".equalsIgnoreCase(request.getAction())) {
            if (assignment.getStatus() != AssignmentStatus.ACTIVE) throw new IllegalStateException("Assignment is already returned");
            assignment.setStatus(AssignmentStatus.RETURNED); assignment.setReturnedDate(LocalDate.now());
            assetService.updateAssetStatus(assignment.getAsset().getId(), AssetStatus.REGISTERED);
        } else if ("transfer".equalsIgnoreCase(request.getAction())) {
            if (assignment.getStatus() != AssignmentStatus.ACTIVE) throw new IllegalStateException("Cannot transfer a returned assignment");
            if (request.getAssigneeName() != null && !request.getAssigneeName().trim().isEmpty()) assignment.setAssigneeName(request.getAssigneeName().trim());
            if (request.getNewStationId() != null) assignment.setStation(stationRepository.findById(request.getNewStationId()).orElseThrow(() -> new ResourceNotFoundException("No station with ID " + request.getNewStationId())));
        } else throw new IllegalArgumentException("Invalid action value: " + request.getAction());
        return toResponse(assignmentRepository.save(assignment));
    }

    private Assignment getAssignmentOrThrow(Long id) { return assignmentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No assignment with ID " + id)); }
    private AssignmentResponse toResponse(Assignment a) {
        AssignmentResponse r = new AssignmentResponse();
        r.setId(a.getId());
        r.setAssetId(a.getAsset().getId());
        if (a.getAsset() != null) {
            r.setAssetSerialNumber(a.getAsset().getSerialNumber());
            r.setAssetType(a.getAsset().getAssetType() != null ? a.getAsset().getAssetType().name() : null);
            if (a.getAsset().getDepartment() != null) {
                r.setDepartmentName(a.getAsset().getDepartment().getName());
            }
        }
        r.setAssigneeName(a.getAssigneeName());
        r.setStationId(a.getStation().getId());
        if (a.getStation() != null) {
            r.setStationName(a.getStation().getName());
        }
        r.setAssignedDate(a.getAssignedDate());
        r.setReturnedDate(a.getReturnedDate());
        r.setStatus(a.getStatus().name());
        return r;
    }
}