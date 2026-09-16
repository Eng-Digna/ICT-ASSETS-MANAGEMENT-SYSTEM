package tz.go.tpa.ict_assets_management;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tz.go.tpa.ict_assets_management.dto.request.CreateOrganizationRequest;
import tz.go.tpa.ict_assets_management.dto.request.UpdateOrganizationRequest;
import tz.go.tpa.ict_assets_management.entity.Organization;
import tz.go.tpa.ict_assets_management.exception.DuplicateResourceException;
import tz.go.tpa.ict_assets_management.exception.ResourceNotFoundException;
import tz.go.tpa.ict_assets_management.repository.OrganizationRepository;
import tz.go.tpa.ict_assets_management.service.impl.OrganizationServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrganizationServiceTest {
    private OrganizationRepository repo;
    private OrganizationServiceImpl service;

    @BeforeEach
    void setUp() {
        repo = mock(OrganizationRepository.class);
        service = new OrganizationServiceImpl(repo);
    }

    @Test
    void createSuccess() {
        CreateOrganizationRequest req = new CreateOrganizationRequest();
        req.setName("Finance");
        req.setCode("FIN");

        when(repo.existsByNameIgnoreCase("Finance")).thenReturn(false);
        when(repo.existsByCodeIgnoreCase("FIN")).thenReturn(false);

        Organization saved = new Organization();
        saved.setId(1L);
        saved.setName("Finance");
        saved.setCode("FIN");

        when(repo.save(any(Organization.class))).thenReturn(saved);

        var resp = service.createOrganization(req);
        assertNotNull(resp);
        assertEquals(1L, resp.getId());
        assertEquals("Finance", resp.getName());
    }

    @Test
    void createDuplicateName() {
        CreateOrganizationRequest req = new CreateOrganizationRequest();
        req.setName("HR");
        req.setCode("HR");

        when(repo.existsByNameIgnoreCase("HR")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> service.createOrganization(req));
    }

    @Test
    void createDuplicateCode() {
        CreateOrganizationRequest req = new CreateOrganizationRequest();
        req.setName("Legal");
        req.setCode("LG");

        when(repo.existsByNameIgnoreCase("Legal")).thenReturn(false);
        when(repo.existsByCodeIgnoreCase("LG")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> service.createOrganization(req));
    }

    @Test
    void parentNotFound() {
        CreateOrganizationRequest req = new CreateOrganizationRequest();
        req.setName("Subs");
        req.setCode("SUB");
        req.setParentId(99L);

        when(repo.existsByNameIgnoreCase("Subs")).thenReturn(false);
        when(repo.existsByCodeIgnoreCase("SUB")).thenReturn(false);
        when(repo.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.createOrganization(req));
    }

    @Test
    void getByIdNotFound() {
        when(repo.findById(42L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.getOrganizationById(42L));
    }

    @Test
    void updateSuccess() {
        Organization org = new Organization();
        org.setId(2L);
        org.setName("Ops");
        org.setCode("OPS");

        when(repo.findById(2L)).thenReturn(Optional.of(org));
        when(repo.existsByNameIgnoreCaseAndIdNot("NewOps", 2L)).thenReturn(false);
        when(repo.existsByCodeIgnoreCaseAndIdNot("NEWOPS", 2L)).thenReturn(false);
        when(repo.save(any(Organization.class))).thenAnswer(i -> i.getArgument(0));

        UpdateOrganizationRequest req = new UpdateOrganizationRequest();
        req.setName("NewOps");
        req.setCode("NEWOPS");

        var resp = service.updateOrganization(2L, req);
        assertEquals(2L, resp.getId());
        assertEquals("NewOps", resp.getName());
    }

    @Test
    void updateDuplicateName() {
        Organization org = new Organization();
        org.setId(6L);
        org.setName("Ops");
        org.setCode("OPS");

        when(repo.findById(6L)).thenReturn(Optional.of(org));
        when(repo.existsByNameIgnoreCaseAndIdNot("NewName", 6L)).thenReturn(true);

        UpdateOrganizationRequest req = new UpdateOrganizationRequest();
        req.setName("NewName");
        req.setCode("OPS");

        assertThrows(DuplicateResourceException.class, () -> service.updateOrganization(6L, req));
    }

    @Test
    void updateSelfParentRejected() {
        Organization org = new Organization();
        org.setId(5L);
        org.setName("Dept");
        org.setCode("DPT");

        when(repo.findById(5L)).thenReturn(Optional.of(org));

        UpdateOrganizationRequest req = new UpdateOrganizationRequest();
        req.setName("Dept");
        req.setCode("DPT");
        req.setParentId(5L);

        assertThrows(IllegalArgumentException.class, () -> service.updateOrganization(5L, req));
    }

    @Test
    void deleteSuccess() {
        Organization org = new Organization();
        org.setId(10L);

        when(repo.findById(10L)).thenReturn(Optional.of(org));

        assertDoesNotThrow(() -> service.deleteOrganization(10L));
        verify(repo).delete(org);
    }

    @Test
    void deleteBlockedByChildren() {
        Organization org = new Organization();
        org.setId(7L);
        org.getChildren().add(new Organization());

        when(repo.findById(7L)).thenReturn(Optional.of(org));

        assertThrows(RuntimeException.class, () -> service.deleteOrganization(7L));
    }

    @Test
    void deleteBlockedByUsers() {
        Organization org = new Organization();
        org.setId(8L);
        org.getUsers().add(null);

        when(repo.findById(8L)).thenReturn(Optional.of(org));

        assertThrows(RuntimeException.class, () -> service.deleteOrganization(8L));
    }
}
