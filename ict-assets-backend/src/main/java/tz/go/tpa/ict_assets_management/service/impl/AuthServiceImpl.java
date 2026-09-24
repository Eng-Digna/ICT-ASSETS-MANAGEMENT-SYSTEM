package tz.go.tpa.ict_assets_management.service.impl;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tz.go.tpa.ict_assets_management.dto.request.LoginRequest;
import tz.go.tpa.ict_assets_management.dto.response.JwtAuthenticationResponse;
import tz.go.tpa.ict_assets_management.entity.User;
import tz.go.tpa.ict_assets_management.repository.UserRepository;
import tz.go.tpa.ict_assets_management.security.JwtUtil;
import tz.go.tpa.ict_assets_management.service.AuthService;

import java.util.List;

@Service
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public AuthServiceImpl(AuthenticationManager authenticationManager, JwtUtil jwtUtil, UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public JwtAuthenticationResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        UserDetails principal = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findByUsername(principal.getUsername())
                .orElseThrow(() -> new AuthenticationException("User not found") {
                });

        List<String> roles = user.getRoles().stream().map(role -> role.getName().name()).toList();
        Long stationId = user.getStation() != null ? user.getStation().getId() : null;
        String stationName = user.getStation() != null ? user.getStation().getName() : null;
        String token = jwtUtil.generateToken(user.getUsername(), roles, stationId);

        return new JwtAuthenticationResponse(token, user.getUsername(), roles, stationId, stationName);
    }
}
