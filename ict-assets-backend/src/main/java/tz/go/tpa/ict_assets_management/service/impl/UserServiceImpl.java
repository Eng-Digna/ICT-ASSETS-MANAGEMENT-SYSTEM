package tz.go.tpa.ict_assets_management.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tz.go.tpa.ict_assets_management.dto.request.ChangePasswordRequest;
import tz.go.tpa.ict_assets_management.dto.request.CreateUserRequest;
import tz.go.tpa.ict_assets_management.dto.request.UpdateUserRequest;
import tz.go.tpa.ict_assets_management.dto.response.UserResponse;
import tz.go.tpa.ict_assets_management.entity.Role;
import tz.go.tpa.ict_assets_management.entity.RoleName;
import tz.go.tpa.ict_assets_management.entity.User;
import tz.go.tpa.ict_assets_management.exception.DuplicateResourceException;
import tz.go.tpa.ict_assets_management.exception.ResourceNotFoundException;
import tz.go.tpa.ict_assets_management.repository.RoleRepository;
import tz.go.tpa.ict_assets_management.repository.UserRepository;
import tz.go.tpa.ict_assets_management.service.UserService;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("Username already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setEnabled(request.isEnabled());

        RoleName requestedRole = request.getRole() == null ? RoleName.REGISTRAR : request.getRole();
        Role role = roleRepository.findByName(requestedRole)
            .orElseGet(() -> roleRepository.save(new Role(requestedRole,
                    requestedRole == RoleName.ADMINISTRATOR ? "System administrator" : "Station-level ICT officer")));
        user.getRoles().add(role);

        return toResponse(userRepository.save(user));
    }

    @Override
    public UserResponse getUserById(Long id) {
        return toResponse(findUserById(id));
    }

    @Override
    public Page<UserResponse> getUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(this::toResponse);
    }

    @Override
    @Transactional
    public UserResponse updateUser(Long id, UpdateUserRequest request) {
        User user = findUserById(id);

        if (request.getUsername() != null && !request.getUsername().equals(user.getUsername())
                && userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("Username already exists");
        }
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())
                && userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already exists");
        }

        if (request.getUsername() != null) user.setUsername(request.getUsername());
        if (request.getEmail() != null) user.setEmail(request.getEmail());
        if (request.getFirstName() != null) user.setFirstName(request.getFirstName());
        if (request.getLastName() != null) user.setLastName(request.getLastName());
        if (request.getEnabled() != null) user.setEnabled(request.getEnabled());

        return toResponse(userRepository.save(user));
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        User user = findUserById(id);
        userRepository.delete(user);
    }

    @Override
    @Transactional
    public UserResponse toggleStatus(Long id, boolean enabled) {
        User user = findUserById(id);
        user.setEnabled(enabled);
        return toResponse(userRepository.save(user));
    }

    @Override
    @Transactional
    public UserResponse changePassword(Long userId, ChangePasswordRequest request) {
        User user = findUserById(userId);
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        return toResponse(userRepository.save(user));
    }

    @Override
    public List<Role> getUserRoles(Long userId) {
        return List.copyOf(findUserById(userId).getRoles());
    }

    @Override
    @Transactional
    public UserResponse assignRole(Long userId, Long roleId) {
        User user = findUserById(userId);
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + roleId));
        user.getRoles().add(role);
        return toResponse(userRepository.save(user));
    }

    @Override
    @Transactional
    public UserResponse removeRole(Long userId, Long roleId) {
        User user = findUserById(userId);
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + roleId));
        user.getRoles().remove(role);
        return toResponse(userRepository.save(user));
    }

    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    private UserResponse toResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setEnabled(user.isEnabled());
        response.setFullName(user.getFullName());
        response.setRoles(user.getRoles().stream().map(Role::getName).map(Enum::name).toList());
        return response;
    }
}
