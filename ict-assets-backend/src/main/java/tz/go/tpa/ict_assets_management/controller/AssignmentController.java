package tz.go.tpa.ict_assets_management.controller;

import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tz.go.tpa.ict_assets_management.dto.request.*;
import tz.go.tpa.ict_assets_management.dto.response.*;
import tz.go.tpa.ict_assets_management.service.AssignmentService;

@RestController
@RequestMapping("/api/v1/assignments")
public class AssignmentController {
    private final AssignmentService assignmentService;
    public AssignmentController(AssignmentService assignmentService) { this.assignmentService = assignmentService; }
    @PostMapping
    @PreAuthorize("hasRole('REGISTRAR') or hasRole('ADMINISTRATOR')")
    public ResponseEntity<AssignmentResponse> assignAsset(@Valid @RequestBody AssignAssetRequest request) { return ResponseEntity.status(HttpStatus.CREATED).body(assignmentService.assignAsset(request)); }
    @GetMapping
    public ResponseEntity<PageResponse<AssignmentResponse>> listAssignments(@RequestParam(required = false) Long assetId, @RequestParam(required = false) String assigneeName, @RequestParam(required = false) String status, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int pageSize) {
        AssignmentSearchFilter filter = new AssignmentSearchFilter(); filter.setAssetId(assetId); filter.setAssigneeName(assigneeName); filter.setStatus(status); filter.setPage(page); filter.setPageSize(pageSize);
        return ResponseEntity.ok(assignmentService.listAssignments(filter));
    }
    @GetMapping("/{id}")
    public ResponseEntity<AssignmentResponse> getAssignment(@PathVariable Long id) { return ResponseEntity.ok(assignmentService.getAssignment(id)); }
    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('REGISTRAR') or hasRole('ADMINISTRATOR')")
    public ResponseEntity<AssignmentResponse> transferOrReturn(@PathVariable Long id, @RequestBody TransferReturnRequest request) { return ResponseEntity.ok(assignmentService.transferOrReturn(id, request)); }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('REGISTRAR') or hasRole('ADMINISTRATOR')")
    public ResponseEntity<Void> deleteAssignment(@PathVariable Long id) {
        assignmentService.deleteActiveAssignment(id);
        return ResponseEntity.noContent().build();
    }
}