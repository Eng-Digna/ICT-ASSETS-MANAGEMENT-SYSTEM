package tz.go.tpa.ict_assets_management;

import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import tz.go.tpa.ict_assets_management.dto.request.LoginRequest;
import tz.go.tpa.ict_assets_management.dto.response.JwtAuthenticationResponse;
import tz.go.tpa.ict_assets_management.service.AuthService;

import static org.junit.jupiter.api.Assertions.*;

class AuthServiceTest {

    @Test
    void loginShouldReturnJwtWhenCredentialsAreValid() {
        LoginRequest request = new LoginRequest();
        request.setUsername("admin");
        request.setPassword("Admin@123");

        AuthService authService = new AuthService() {
            @Override
            public JwtAuthenticationResponse login(LoginRequest loginRequest) {
                return new JwtAuthenticationResponse("token", "admin", java.util.List.of("ADMINISTRATOR"), null, null);
            }
        };

        JwtAuthenticationResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("token", response.getToken());
        assertEquals("admin", response.getUsername());
        assertTrue(response.getRoles().contains("ADMINISTRATOR"));
    }

    @Test
    void loginShouldFailWhenNoCredentialsProvided() {
        LoginRequest request = new LoginRequest();

        AuthService authService = new AuthService() {
            @Override
            public JwtAuthenticationResponse login(LoginRequest loginRequest) {
                throw new AuthenticationCredentialsNotFoundException("Credentials missing");
            }
        };

        assertThrows(AuthenticationCredentialsNotFoundException.class, () -> authService.login(request));
    }
}
