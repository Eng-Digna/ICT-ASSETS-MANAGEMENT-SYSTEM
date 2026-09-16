package tz.go.tpa.ict_assets_management.service;

import tz.go.tpa.ict_assets_management.dto.request.*;
import tz.go.tpa.ict_assets_management.dto.response.AssignmentResponse;
import tz.go.tpa.ict_assets_management.dto.response.PageResponse;

public interface AssignmentService {
    AssignmentResponse assignAsset(AssignAssetRequest request);
    PageResponse<AssignmentResponse> listAssignments(AssignmentSearchFilter filter);
    AssignmentResponse getAssignment(Long id);
    AssignmentResponse transferOrReturn(Long id, TransferReturnRequest request);
}