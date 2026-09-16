package tz.go.tpa.ict_assets_management.service;

import jakarta.validation.Valid;
import tz.go.tpa.ict_assets_management.dto.request.LoginRequest;
import tz.go.tpa.ict_assets_management.dto.response.JwtAuthenticationResponse;

public interface AuthService {
    JwtAuthenticationResponse login(@Valid LoginRequest request);
}
