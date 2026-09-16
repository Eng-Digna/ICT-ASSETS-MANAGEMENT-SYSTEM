package tz.go.tpa.ict_assets_management.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tz.go.tpa.ict_assets_management.dto.request.*;
import tz.go.tpa.ict_assets_management.dto.response.*;
import tz.go.tpa.ict_assets_management.service.DisposalService;

@RestController
@RequestMapping("/api/v1/disposal-requests")
public class DisposalController {
    private final DisposalService disposalService;
    public DisposalController(DisposalService disposalService) { this.disposalService = disposalService; }
    @PostMapping
    @PreAuthorize("hasRole('REGISTRAR') or hasRole('ADMINISTRATOR')")
    public ResponseEntity<DisposalRequestResponse> submitRequest(@Valid @RequestBody SubmitDisposalRequest request) { return ResponseEntity.status(HttpStatus.CREATED).body(disposalService.submitRequest(request)); }
    @GetMapping
    public ResponseEntity<PageResponse<DisposalRequestResponse>> listRequests(@RequestParam(required = false) String status, @RequestParam(required = false) Long assetId, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int pageSize) {
        DisposalSearchFilter filter = new DisposalSearchFilter(); filter.setStatus(status); filter.setAssetId(assetId); filter.setPage(page); filter.setPageSize(pageSize);
        return ResponseEntity.ok(disposalService.listRequests(filter));
    }
    @GetMapping("/{id}")
    public ResponseEntity<DisposalRequestResponse> getRequest(@PathVariable Long id) { return ResponseEntity.ok(disposalService.getRequest(id)); }
    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<DisposalRequestResponse> decide(@PathVariable Long id, @Valid @RequestBody DisposalDecisionRequest request) { return ResponseEntity.ok(disposalService.decide(id, request)); }
}