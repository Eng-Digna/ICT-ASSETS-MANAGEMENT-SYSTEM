package tz.go.tpa.ict_assets_management;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import tz.go.tpa.ict_assets_management.dto.request.CreateUserRequest;
import tz.go.tpa.ict_assets_management.entity.Role;
import tz.go.tpa.ict_assets_management.entity.RoleName;
import tz.go.tpa.ict_assets_management.entity.User;
import tz.go.tpa.ict_assets_management.repository.RoleRepository;
import tz.go.tpa.ict_assets_management.repository.UserRepository;
import tz.go.tpa.ict_assets_management.service.impl.UserServiceImpl;
import tz.go.tpa.ict_assets_management.repository.StationRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Test
    void createUserShouldHashPasswordAndAssignDefaultRole() {
        UserRepository userRepository = mock(UserRepository.class);
        RoleRepository roleRepository = mock(RoleRepository.class);
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        StationRepository stationRepository = mock(StationRepository.class);

        when(userRepository.existsByUsername("alice")).thenReturn(false);
        when(userRepository.existsByEmail("alice@example.com")).thenReturn(false);
        when(roleRepository.findByName(RoleName.REGISTRAR)).thenReturn(Optional.of(new Role(RoleName.REGISTRAR, "Station-level ICT officer")));

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUsername("alice");
        savedUser.setEmail("alice@example.com");
        savedUser.setFirstName("Alice");
        savedUser.setLastName("Wang");
        savedUser.setPasswordHash(passwordEncoder.encode("Secret@123"));
        savedUser.setEnabled(true);
        savedUser.getRoles().add(new Role(RoleName.REGISTRAR, "Station-level ICT officer"));

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserServiceImpl service = new UserServiceImpl(userRepository, roleRepository, passwordEncoder, stationRepository);
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("alice");
        request.setEmail("alice@example.com");
        request.setFirstName("Alice");
        request.setLastName("Wang");
        request.setPassword("Secret@123");

        var response = service.createUser(request);

        assertNotNull(response);
        assertEquals("alice", response.getUsername());
        assertTrue(passwordEncoder.matches("Secret@123", savedUser.getPasswordHash()));
        assertTrue(response.getRoles().contains("REGISTRAR"));
    }
}
