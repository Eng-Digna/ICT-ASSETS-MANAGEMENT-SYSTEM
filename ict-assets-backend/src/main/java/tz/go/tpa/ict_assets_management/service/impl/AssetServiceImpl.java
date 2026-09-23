package tz.go.tpa.ict_assets_management.service.impl;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import tz.go.tpa.ict_assets_management.dto.request.AssetSearchFilter;
import tz.go.tpa.ict_assets_management.dto.request.RegisterAssetRequest;
import tz.go.tpa.ict_assets_management.dto.request.UpdateAssetRequest;
import tz.go.tpa.ict_assets_management.dto.response.AssetResponse;
import tz.go.tpa.ict_assets_management.dto.response.PageResponse;
import tz.go.tpa.ict_assets_management.entity.Asset;
import tz.go.tpa.ict_assets_management.entity.Department;
import tz.go.tpa.ict_assets_management.entity.Station;
import tz.go.tpa.ict_assets_management.enums.AssetStatus;
import tz.go.tpa.ict_assets_management.enums.AssetType;
import tz.go.tpa.ict_assets_management.exception.DuplicateResourceException;
import tz.go.tpa.ict_assets_management.exception.ResourceNotFoundException;
import tz.go.tpa.ict_assets_management.repository.AssetRepository;
import tz.go.tpa.ict_assets_management.repository.DepartmentRepository;
import tz.go.tpa.ict_assets_management.repository.StationRepository;
import tz.go.tpa.ict_assets_management.service.AssetService;

import java.util.ArrayList;
import java.util.List;

@Service
public class AssetServiceImpl implements AssetService {

    private final AssetRepository assetRepository;
    private final DepartmentRepository departmentRepository;
    private final StationRepository stationRepository;

    public AssetServiceImpl(AssetRepository assetRepository,
            DepartmentRepository departmentRepository,
            StationRepository stationRepository) {
        this.assetRepository = assetRepository;
        this.departmentRepository = departmentRepository;
        this.stationRepository = stationRepository;
    }

    @Override
    public AssetResponse registerAsset(RegisterAssetRequest request) {
        if (assetRepository.existsBySerialNumber(request.getSerialNumber())) {
            throw new DuplicateResourceException(
                    "Serial number '" + request.getSerialNumber() + "' already exists.");
        }

        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department not found"));
        Station station = stationRepository.findById(request.getStationId())
                .orElseThrow(() -> new ResourceNotFoundException("Station not found"));

        Asset asset = new Asset();
        asset.setAssetType(AssetType.valueOf(request.getAssetType().toUpperCase()));
        asset.setSerialNumber(request.getSerialNumber());
        asset.setMacAddress(request.getMacAddress());
        asset.setBrand(request.getBrand());
        asset.setModel(request.getModel());
        asset.setOperatingSystem(request.getOperatingSystem());
        asset.setWarrantyStartDate(request.getWarrantyStartDate());
        asset.setWarrantyEndDate(request.getWarrantyEndDate());
        asset.setDepartment(department);
        asset.setStation(station);
        asset.setStatus(AssetStatus.REGISTERED);

        return toResponse(assetRepository.save(asset));
    }

    @Override
    public PageResponse<AssetResponse> listAssets(AssetSearchFilter filter) {
        Specification<Asset> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (filter.getDepartmentId() != null)
                predicates.add(cb.equal(root.get("department").get("id"), filter.getDepartmentId()));
            if (filter.getStationId() != null)
                predicates.add(cb.equal(root.get("station").get("id"), filter.getStationId()));
            if (filter.getStatus() != null)
                predicates.add(cb.equal(root.get("status"), AssetStatus.valueOf(filter.getStatus().toUpperCase())));
            if (filter.getAssetType() != null)
                predicates.add(cb.equal(root.get("assetType"), AssetType.valueOf(filter.getAssetType().toUpperCase())));
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<Asset> result = assetRepository.findAll(spec, PageRequest.of(filter.getPage(), filter.getPageSize()));
        List<AssetResponse> data = result.getContent().stream().map(this::toResponse).toList();
        return new PageResponse<>(data, filter.getPage() + 1, filter.getPageSize(), result.getTotalElements());
    }

    @Override
    public AssetResponse getAsset(Long id) {
        return toResponse(getAssetEntityOrThrow(id));
    }

    @Override
    public AssetResponse updateAsset(Long id, UpdateAssetRequest request) {
        Asset asset = getAssetEntityOrThrow(id);
        if (request.getBrand() != null)
            asset.setBrand(request.getBrand());
        if (request.getModel() != null)
            asset.setModel(request.getModel());
        if (request.getOperatingSystem() != null)
            asset.setOperatingSystem(request.getOperatingSystem());
        if (request.getWarrantyEndDate() != null)
            asset.setWarrantyEndDate(request.getWarrantyEndDate());
        return toResponse(assetRepository.save(asset));
    }

    @Override
    public Asset getAssetEntityOrThrow(Long id) {
        return assetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No asset with ID " + id));
    }

    @Override
    public void updateAssetStatus(Long id, AssetStatus newStatus) {
        Asset asset = getAssetEntityOrThrow(id);
        asset.setStatus(newStatus);
        assetRepository.save(asset);
    }

    private AssetResponse toResponse(Asset asset) {
        AssetResponse r = new AssetResponse();
        r.setId(asset.getId());
        r.setAssetType(asset.getAssetType().name());
        r.setType(asset.getAssetType().name());
        r.setSerialNumber(asset.getSerialNumber());
        r.setMacAddress(asset.getMacAddress());
        r.setBrand(asset.getBrand());
        r.setModel(asset.getModel());
        r.setOperatingSystem(asset.getOperatingSystem());
        r.setWarrantyStartDate(asset.getWarrantyStartDate());
        r.setWarrantyEndDate(asset.getWarrantyEndDate());
        r.setStatus(asset.getStatus().name());
        r.setDepartmentId(asset.getDepartment().getId());
        r.setStationId(asset.getStation().getId());
        r.setDepartment(asset.getDepartment().getName());
        r.setStation(asset.getStation().getName());
        return r;
    }
}
