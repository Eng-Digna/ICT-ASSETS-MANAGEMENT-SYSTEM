package tz.go.tpa.ict_assets_management.service;

import tz.go.tpa.ict_assets_management.dto.request.*;
import tz.go.tpa.ict_assets_management.dto.response.DisposalRequestResponse;
import tz.go.tpa.ict_assets_management.dto.response.PageResponse;

public interface DisposalService {
    DisposalRequestResponse submitRequest(SubmitDisposalRequest request);
    PageResponse<DisposalRequestResponse> listRequests(DisposalSearchFilter filter);
    DisposalRequestResponse getRequest(Long id);
    DisposalRequestResponse decide(Long id, DisposalDecisionRequest request);
}