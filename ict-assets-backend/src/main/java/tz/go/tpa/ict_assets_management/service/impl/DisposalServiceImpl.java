package tz.go.tpa.ict_assets_management.service.impl;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
public class DisposalServiceImpl implements DisposalService {
    private final DisposalRequestRepository disposalRequestRepository;
    private final AssetService assetService;
    private final UserRepository userRepository;

    public DisposalServiceImpl(DisposalRequestRepository disposalRequestRepository, AssetService assetService, UserRepository userRepository) {
        this.disposalRequestRepository = disposalRequestRepository;
        this.assetService = assetService;
        this.userRepository = userRepository;
    }

    @Override @Transactional
    public DisposalRequestResponse submitRequest(SubmitDisposalRequest request) {
        Asset asset = assetService.getAssetEntityOrThrow(request.getAssetId());
        if (disposalRequestRepository.existsByAssetIdAndStatus(asset.getId(), DisposalStatus.PENDING)
            || asset.getStatus() != AssetStatus.REGISTERED) {
            throw new DuplicateResourceException("Asset is not available for a new disposal request");
        }
        User requestedBy = tz.go.tpa.ict_assets_management.util.CurrentUser.required();
        DisposalRequest disposal = new DisposalRequest();
        disposal.setAsset(asset); disposal.setRequestedBy(requestedBy); disposal.setRequestDate(LocalDate.now());
        disposal.setReason(request.getReason()); disposal.setStatus(DisposalStatus.PENDING);
        assetService.updateAssetStatus(asset.getId(), AssetStatus.DISPOSAL_REQUESTED);
        return toResponse(disposalRequestRepository.save(disposal));
    }

    @Override @Transactional(readOnly = true)
    public PageResponse<DisposalRequestResponse> listRequests(DisposalSearchFilter filter) {
        Specification<DisposalRequest> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (filter.getStatus() != null) predicates.add(cb.equal(root.get("status"), DisposalStatus.valueOf(filter.getStatus().toUpperCase())));
            if (filter.getAssetId() != null) predicates.add(cb.equal(root.get("asset").get("id"), filter.getAssetId()));
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        Page<DisposalRequest> result = disposalRequestRepository.findAll(spec, PageRequest.of(filter.getPage(), filter.getPageSize()));
        return new PageResponse<>(result.getContent().stream().map(this::toResponse).toList(), filter.getPage() + 1, filter.getPageSize(), result.getTotalElements());
    }

    @Override @Transactional(readOnly = true)
    public DisposalRequestResponse getRequest(Long id) { return toResponse(getRequestOrThrow(id)); }

    @Override @Transactional
    public DisposalRequestResponse decide(Long id, DisposalDecisionRequest request) {
        DisposalRequest disposal = getRequestOrThrow(id);
        if (disposal.getStatus() != DisposalStatus.PENDING) throw new DuplicateResourceException("Disposal request has already been decided");
        User approvedBy = tz.go.tpa.ict_assets_management.util.CurrentUser.required();
        if (approvedBy.getId().equals(disposal.getRequestedBy().getId())) {
            throw new IllegalStateException("The requester cannot decide their own disposal request");
        }
        if (disposal.getAsset().getStatus() != AssetStatus.DISPOSAL_REQUESTED) {
            throw new IllegalStateException("Asset is no longer pending disposal");
        }
        if ("approve".equalsIgnoreCase(request.getDecision())) {
            disposal.setStatus(DisposalStatus.APPROVED);
            assetService.updateAssetStatus(disposal.getAsset().getId(), AssetStatus.DISPOSED);
        } else if ("reject".equalsIgnoreCase(request.getDecision())) {
            disposal.setStatus(DisposalStatus.REJECTED);
            assetService.updateAssetStatus(disposal.getAsset().getId(), AssetStatus.REGISTERED);
        } else throw new IllegalArgumentException("Invalid decision value: " + request.getDecision());
        disposal.setApprovedBy(approvedBy); disposal.setApprovalDate(LocalDate.now()); disposal.setComments(request.getComments());
        return toResponse(disposalRequestRepository.save(disposal));
    }

    private DisposalRequest getRequestOrThrow(Long id) {
        return disposalRequestRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No disposal request with ID " + id));
    }

    private DisposalRequestResponse toResponse(DisposalRequest d) {
        DisposalRequestResponse response = new DisposalRequestResponse();
        response.setId(d.getId());
        response.setAssetId(d.getAsset().getId());
        if (d.getAsset() != null) {
            response.setAssetSerialNumber(d.getAsset().getSerialNumber());
            response.setAssetType(d.getAsset().getAssetType() != null ? d.getAsset().getAssetType().name() : null);
            if (d.getAsset().getDepartment() != null) {
                response.setDepartmentName(d.getAsset().getDepartment().getName());
            }
            if (d.getAsset().getStation() != null) {
                response.setStationName(d.getAsset().getStation().getName());
            }
        }
        response.setRequestedBy(d.getRequestedBy().getId());
        if (d.getRequestedBy() != null) {
            String name = ((d.getRequestedBy().getFirstName() != null ? d.getRequestedBy().getFirstName() : "") + " " +
                    (d.getRequestedBy().getLastName() != null ? d.getRequestedBy().getLastName() : "")).trim();
            response.setRequestedByName(!name.isEmpty() ? name : d.getRequestedBy().getUsername());
        }
        response.setRequestDate(d.getRequestDate());
        response.setStatus(d.getStatus().name());
        response.setReason(d.getReason());
        if (d.getApprovedBy() != null) {
            response.setApprovedBy(d.getApprovedBy().getId());
            String appName = ((d.getApprovedBy().getFirstName() != null ? d.getApprovedBy().getFirstName() : "") + " " +
                    (d.getApprovedBy().getLastName() != null ? d.getApprovedBy().getLastName() : "")).trim();
            response.setApprovedByName(!appName.isEmpty() ? appName : d.getApprovedBy().getUsername());
        }
        response.setApprovalDate(d.getApprovalDate());
        return response;
    }
}