package tz.go.tpa.ict_assets_management.controller;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tz.go.tpa.ict_assets_management.dto.request.CreateOrganizationRequest;
import tz.go.tpa.ict_assets_management.dto.request.UpdateOrganizationRequest;
import tz.go.tpa.ict_assets_management.dto.response.ApiResponse;
import tz.go.tpa.ict_assets_management.dto.response.OrganizationResponse;
import tz.go.tpa.ict_assets_management.service.OrganizationService;

@RestController
@RequestMapping("/api/v1/organizations")
public class OrganizationController {
    private final OrganizationService organizationService;

    public OrganizationController(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<OrganizationResponse>> create(@Valid @RequestBody CreateOrganizationRequest request) {
        OrganizationResponse r = organizationService.createOrganization(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Organization created successfully", r, "/api/v1/organizations"));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<Page<OrganizationResponse>>> list(Pageable pageable) {
        Page<OrganizationResponse> page = organizationService.getOrganizations(pageable);
        return ResponseEntity.ok(new ApiResponse<>(true, "Organizations retrieved successfully", page, "/api/v1/organizations"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<OrganizationResponse>> get(@PathVariable Long id) {
        OrganizationResponse r = organizationService.getOrganizationById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Organization retrieved successfully", r, "/api/v1/organizations/" + id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<OrganizationResponse>> update(@PathVariable Long id, @Valid @RequestBody UpdateOrganizationRequest request) {
        OrganizationResponse r = organizationService.updateOrganization(id, request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Organization updated successfully", r, "/api/v1/organizations/" + id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        organizationService.deleteOrganization(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Organization deleted successfully", null, "/api/v1/organizations/" + id));
    }
}
