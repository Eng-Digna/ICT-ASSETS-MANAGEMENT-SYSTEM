package tz.go.tpa.ict_assets_management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tz.go.tpa.ict_assets_management.entity.Department;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
}