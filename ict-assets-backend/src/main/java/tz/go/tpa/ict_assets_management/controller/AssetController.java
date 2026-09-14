package tz.go.tpa.ict_assets_management.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tz.go.tpa.ict_assets_management.dto.request.AssetSearchFilter;
import tz.go.tpa.ict_assets_management.dto.request.RegisterAssetRequest;
import tz.go.tpa.ict_assets_management.dto.request.UpdateAssetRequest;
import tz.go.tpa.ict_assets_management.dto.response.AssetResponse;
import tz.go.tpa.ict_assets_management.dto.response.PageResponse;
import tz.go.tpa.ict_assets_management.service.AssetService;

@RestController
@RequestMapping("/api/v1/assets")
public class AssetController {

    private final AssetService assetService;

    public AssetController(AssetService assetService) {
        this.assetService = assetService;
    }

    @PostMapping
    public ResponseEntity<AssetResponse> registerAsset(@Valid @RequestBody RegisterAssetRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(assetService.registerAsset(request));
    }

    @GetMapping
    public ResponseEntity<PageResponse<AssetResponse>> listAssets(
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) Long stationId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String assetType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        AssetSearchFilter filter = new AssetSearchFilter();
        filter.setDepartmentId(departmentId);
        filter.setStationId(stationId);
        filter.setStatus(status);
        filter.setAssetType(assetType);
        filter.setPage(page);
        filter.setPageSize(pageSize);
        return ResponseEntity.ok(assetService.listAssets(filter));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AssetResponse> getAsset(@PathVariable Long id) {
        return ResponseEntity.ok(assetService.getAsset(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<AssetResponse> updateAsset(@PathVariable Long id, @RequestBody UpdateAssetRequest request) {
        return ResponseEntity.ok(assetService.updateAsset(id, request));
    }
}