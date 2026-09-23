package tz.go.tpa.ict_assets_management.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tz.go.tpa.ict_assets_management.dto.response.ApiResponse;
import tz.go.tpa.ict_assets_management.entity.Department;
import tz.go.tpa.ict_assets_management.repository.DepartmentRepository;

import java.util.List;

@RestController
@RequestMapping("/api/v1/departments")
public class DepartmentController {
    private final DepartmentRepository departmentRepository;

    public DepartmentController(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    @GetMapping
    @PreAuthorize("hasRole('REGISTRAR') or hasRole('ADMINISTRATOR')")
    public ResponseEntity<ApiResponse<List<Department>>> getDepartments() {
        List<Department> list = departmentRepository.findAll();
        return ResponseEntity.ok(new ApiResponse<>(true, "Departments retrieved successfully", list, "/api/v1/departments"));
    }
}
