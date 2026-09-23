package tz.go.tpa.ict_assets_management.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tz.go.tpa.ict_assets_management.dto.response.ApiResponse;
import tz.go.tpa.ict_assets_management.entity.Station;
import tz.go.tpa.ict_assets_management.repository.StationRepository;

import java.util.List;

@RestController
@RequestMapping("/api/v1/stations")
public class StationController {
    private final StationRepository stationRepository;

    public StationController(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    @GetMapping
    @PreAuthorize("hasRole('REGISTRAR') or hasRole('ADMINISTRATOR')")
    public ResponseEntity<ApiResponse<List<Station>>> getStations() {
        List<Station> list = stationRepository.findAll();
        return ResponseEntity.ok(new ApiResponse<>(true, "Stations retrieved successfully", list, "/api/v1/stations"));
    }
}
