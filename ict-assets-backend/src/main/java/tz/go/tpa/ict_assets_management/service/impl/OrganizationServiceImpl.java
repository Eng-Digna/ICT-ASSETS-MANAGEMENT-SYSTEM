package tz.go.tpa.ict_assets_management.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tz.go.tpa.ict_assets_management.dto.request.CreateOrganizationRequest;
import tz.go.tpa.ict_assets_management.dto.request.UpdateOrganizationRequest;
import tz.go.tpa.ict_assets_management.dto.response.OrganizationResponse;
import tz.go.tpa.ict_assets_management.entity.Organization;
import tz.go.tpa.ict_assets_management.exception.DuplicateResourceException;
import tz.go.tpa.ict_assets_management.exception.ResourceNotFoundException;
import tz.go.tpa.ict_assets_management.repository.OrganizationRepository;
import tz.go.tpa.ict_assets_management.service.OrganizationService;

@Service
public class OrganizationServiceImpl implements OrganizationService {
    private final OrganizationRepository organizationRepository;

    public OrganizationServiceImpl(OrganizationRepository organizationRepository) {
        this.organizationRepository = organizationRepository;
    }

    @Override
    @Transactional
    public OrganizationResponse createOrganization(CreateOrganizationRequest request) {
        String name = request.getName() != null ? request.getName().trim() : null;
        String code = request.getCode() != null ? request.getCode().trim() : null;

        if (name == null || name.isBlank()) throw new IllegalArgumentException("Name is required");
        if (code == null || code.isBlank()) throw new IllegalArgumentException("Code is required");

        if (organizationRepository.existsByNameIgnoreCase(name)) {
            throw new DuplicateResourceException("Organization name already exists");
        }
        if (organizationRepository.existsByCodeIgnoreCase(code)) {
            throw new DuplicateResourceException("Organization code already exists");
        }

        Organization org = new Organization();
        org.setName(name);
        org.setCode(code);
        org.setDescription(request.getDescription());

        if (request.getParentId() != null) {
            Organization parent = organizationRepository.findById(request.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent organization not found with id: " + request.getParentId()));
            org.setParent(parent);
        }

        Organization saved = organizationRepository.save(org);
        return toResponse(saved);
    }

    @Override
    public OrganizationResponse getOrganizationById(Long id) {
        return toResponse(findByIdOrThrow(id));
    }

    @Override
    public Page<OrganizationResponse> getOrganizations(Pageable pageable) {
        return organizationRepository.findAll(pageable).map(this::toResponse);
    }

    @Override
    @Transactional
    public OrganizationResponse updateOrganization(Long id, UpdateOrganizationRequest request) {
        Organization org = findByIdOrThrow(id);

        String name = request.getName() != null ? request.getName().trim() : null;
        String code = request.getCode() != null ? request.getCode().trim() : null;

        if (name == null || name.isBlank()) throw new IllegalArgumentException("Name is required");
        if (code == null || code.isBlank()) throw new IllegalArgumentException("Code is required");

        if (request.getParentId() != null && request.getParentId().equals(id)) {
            throw new IllegalArgumentException("Organization cannot be its own parent");
        }

        if (!org.getName().equalsIgnoreCase(name) && organizationRepository.existsByNameIgnoreCaseAndIdNot(name, id)) {
            throw new DuplicateResourceException("Organization name already exists");
        }

        if (!org.getCode().equalsIgnoreCase(code) && organizationRepository.existsByCodeIgnoreCaseAndIdNot(code, id)) {
            throw new DuplicateResourceException("Organization code already exists");
        }

        org.setName(name);
        org.setCode(code);
        org.setDescription(request.getDescription());

        if (request.getParentId() != null) {
            Organization parent = organizationRepository.findById(request.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent organization not found with id: " + request.getParentId()));
            org.setParent(parent);
        } else {
            org.setParent(null);
        }

        Organization saved = organizationRepository.save(org);
        return toResponse(saved);
    }

    @Override
    @Transactional
    public void deleteOrganization(Long id) {
        Organization org = findByIdOrThrow(id);

        if (org.getChildren() != null && !org.getChildren().isEmpty()) {
            throw new DuplicateResourceException("Organization has child organizations and cannot be deleted");
        }

        if (org.getUsers() != null && !org.getUsers().isEmpty()) {
            throw new DuplicateResourceException("Organization has users assigned and cannot be deleted");
        }

        organizationRepository.delete(org);
    }

    private Organization findByIdOrThrow(Long id) {
        return organizationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found with id: " + id));
    }

    private OrganizationResponse toResponse(Organization org) {
        OrganizationResponse r = new OrganizationResponse();
        r.setId(org.getId());
        r.setName(org.getName());
        r.setCode(org.getCode());
        r.setDescription(org.getDescription());
        if (org.getParent() != null) {
            r.setParentId(org.getParent().getId());
            r.setParentName(org.getParent().getName());
        }
        return r;
    }
}
