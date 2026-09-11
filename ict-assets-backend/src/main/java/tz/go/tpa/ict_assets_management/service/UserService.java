package tz.go.tpa.ict_assets_management.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import tz.go.tpa.ict_assets_management.dto.request.ChangePasswordRequest;
import tz.go.tpa.ict_assets_management.dto.request.CreateUserRequest;
import tz.go.tpa.ict_assets_management.dto.request.UpdateUserRequest;
import tz.go.tpa.ict_assets_management.dto.response.UserResponse;
import tz.go.tpa.ict_assets_management.entity.Role;

import java.util.List;

public interface UserService {
    UserResponse createUser(CreateUserRequest request);

    UserResponse getUserById(Long id);

    Page<UserResponse> getUsers(Pageable pageable);

    UserResponse updateUser(Long id, UpdateUserRequest request);

    void deleteUser(Long id);

    UserResponse toggleStatus(Long id, boolean enabled);

    UserResponse changePassword(Long userId, ChangePasswordRequest request);

    List<Role> getUserRoles(Long userId);

    UserResponse assignRole(Long userId, Long roleId);

    UserResponse removeRole(Long userId, Long roleId);
}
