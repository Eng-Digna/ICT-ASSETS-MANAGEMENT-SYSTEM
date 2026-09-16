package tz.go.tpa.ict_assets_management.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import tz.go.tpa.ict_assets_management.dto.request.CreateOrganizationRequest;
import tz.go.tpa.ict_assets_management.dto.request.UpdateOrganizationRequest;
import tz.go.tpa.ict_assets_management.dto.response.OrganizationResponse;

public interface OrganizationService {
    OrganizationResponse createOrganization(CreateOrganizationRequest request);

    OrganizationResponse getOrganizationById(Long id);

    Page<OrganizationResponse> getOrganizations(Pageable pageable);

    OrganizationResponse updateOrganization(Long id, UpdateOrganizationRequest request);

    void deleteOrganization(Long id);
}
