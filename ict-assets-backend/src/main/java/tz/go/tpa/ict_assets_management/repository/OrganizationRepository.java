package tz.go.tpa.ict_assets_management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tz.go.tpa.ict_assets_management.entity.Organization;

public interface OrganizationRepository extends JpaRepository<Organization, Long> {
    boolean existsByNameIgnoreCase(String name);

    boolean existsByCodeIgnoreCase(String code);

    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);

    boolean existsByCodeIgnoreCaseAndIdNot(String code, Long id);
}
