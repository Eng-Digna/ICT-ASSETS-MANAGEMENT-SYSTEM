package tz.go.tpa.ict_assets_management.service.impl;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tz.go.tpa.ict_assets_management.dto.request.LogMaintenanceRequest;
import tz.go.tpa.ict_assets_management.dto.request.MaintenanceSearchFilter;
import tz.go.tpa.ict_assets_management.dto.response.MaintenanceRecordResponse;
import tz.go.tpa.ict_assets_management.dto.response.PageResponse;
import tz.go.tpa.ict_assets_management.entity.Asset;
import tz.go.tpa.ict_assets_management.entity.MaintenanceRecord;
import tz.go.tpa.ict_assets_management.entity.User;
import tz.go.tpa.ict_assets_management.enums.AssetStatus;
import tz.go.tpa.ict_assets_management.exception.ResourceNotFoundException;
import tz.go.tpa.ict_assets_management.repository.MaintenanceRecordRepository;
import tz.go.tpa.ict_assets_management.repository.UserRepository;
import tz.go.tpa.ict_assets_management.service.AssetService;
import tz.go.tpa.ict_assets_management.service.MaintenanceService;

import java.util.ArrayList;
import java.util.List;

@Service
public class MaintenanceServiceImpl implements MaintenanceService {
    private final MaintenanceRecordRepository maintenanceRecordRepository;
    private final AssetService assetService;
    private final UserRepository userRepository;

    public MaintenanceServiceImpl(MaintenanceRecordRepository maintenanceRecordRepository,
                                   AssetService assetService, UserRepository userRepository) {
        this.maintenanceRecordRepository = maintenanceRecordRepository;
        this.assetService = assetService;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public MaintenanceRecordResponse logMaintenance(LogMaintenanceRequest request) {
        Asset asset = assetService.getAssetEntityOrThrow(request.getAssetId());
        if (asset.getStatus() == AssetStatus.UNDER_MAINTENANCE
                || asset.getStatus() == AssetStatus.DISPOSED
                || asset.getStatus() == AssetStatus.DISPOSAL_REQUESTED) {
            throw new IllegalStateException("Asset is not available for maintenance");
        }
        User loggedBy = tz.go.tpa.ict_assets_management.util.CurrentUser.required();

        MaintenanceRecord record = new MaintenanceRecord();
        record.setAsset(asset);
        record.setServiceDate(request.getServiceDate());
        record.setDescription(request.getDescription());
        record.setLoggedBy(loggedBy);
        assetService.updateAssetStatus(asset.getId(), AssetStatus.UNDER_MAINTENANCE);
        return toResponse(maintenanceRecordRepository.save(record));
    }

    @Override
    @Transactional
    public MaintenanceRecordResponse completeMaintenance(Long id) {
        MaintenanceRecord record = maintenanceRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No maintenance record with ID " + id));
        Asset asset = record.getAsset();
        if (asset.getStatus() != AssetStatus.UNDER_MAINTENANCE) {
            throw new IllegalStateException("Asset is not currently under maintenance");
        }
        assetService.updateAssetStatus(asset.getId(), AssetStatus.REGISTERED);
        return toResponse(record);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<MaintenanceRecordResponse> listRecords(MaintenanceSearchFilter filter) {
        Specification<MaintenanceRecord> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (filter.getAssetId() != null) predicates.add(cb.equal(root.get("asset").get("id"), filter.getAssetId()));
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        Page<MaintenanceRecord> result = maintenanceRecordRepository.findAll(spec, PageRequest.of(filter.getPage(), filter.getPageSize()));
        return new PageResponse<>(result.getContent().stream().map(this::toResponse).toList(), filter.getPage() + 1, filter.getPageSize(), result.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public MaintenanceRecordResponse getRecord(Long id) {
        return toResponse(maintenanceRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No maintenance record with ID " + id)));
    }

    private MaintenanceRecordResponse toResponse(MaintenanceRecord record) {
        MaintenanceRecordResponse response = new MaintenanceRecordResponse();
        response.setId(record.getId());
        response.setAssetId(record.getAsset().getId());
        response.setServiceDate(record.getServiceDate());
        response.setDescription(record.getDescription());
        response.setLoggedBy(record.getLoggedBy().getId());
        return response;
    }
}