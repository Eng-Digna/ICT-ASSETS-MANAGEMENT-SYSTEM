package tz.go.tpa.ict_assets_management.service;

import tz.go.tpa.ict_assets_management.dto.request.AssetSearchFilter;
import tz.go.tpa.ict_assets_management.dto.request.RegisterAssetRequest;
import tz.go.tpa.ict_assets_management.dto.request.UpdateAssetRequest;
import tz.go.tpa.ict_assets_management.dto.response.AssetResponse;
import tz.go.tpa.ict_assets_management.dto.response.PageResponse;
import tz.go.tpa.ict_assets_management.entity.Asset;
import tz.go.tpa.ict_assets_management.enums.AssetStatus;

public interface AssetService {
    AssetResponse registerAsset(RegisterAssetRequest request);

    PageResponse<AssetResponse> listAssets(AssetSearchFilter filter);

    AssetResponse getAsset(Long id);

    AssetResponse updateAsset(Long id, UpdateAssetRequest request);

    // used internally by Assignment/Maintenance/Disposal services
    Asset getAssetEntityOrThrow(Long id);

    void updateAssetStatus(Long id, AssetStatus newStatus);

    void deleteAsset(Long id);
}
