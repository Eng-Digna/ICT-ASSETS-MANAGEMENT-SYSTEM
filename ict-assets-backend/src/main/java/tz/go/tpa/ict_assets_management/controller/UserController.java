package tz.go.tpa.ict_assets_management.controller;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tz.go.tpa.ict_assets_management.dto.request.ChangePasswordRequest;
import tz.go.tpa.ict_assets_management.dto.request.CreateUserRequest;
import tz.go.tpa.ict_assets_management.dto.request.UpdateUserRequest;
import tz.go.tpa.ict_assets_management.dto.response.ApiResponse;
import tz.go.tpa.ict_assets_management.dto.response.UserResponse;
import tz.go.tpa.ict_assets_management.entity.Role;
import tz.go.tpa.ict_assets_management.entity.User;
import tz.go.tpa.ict_assets_management.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<ApiResponse<UserResponse>> createUser(@Valid @RequestBody CreateUserRequest request) {
        UserResponse user = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "User created successfully", user, "/api/v1/users"));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<ApiResponse<Page<UserResponse>>> getUsers(Pageable pageable) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Users retrieved successfully", userService.getUsers(pageable), "/api/v1/users"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<ApiResponse<UserResponse>> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponse<>(true, "User retrieved successfully", userService.getUserById(id), "/api/v1/users/" + id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(@PathVariable Long id, @Valid @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(new ApiResponse<>(true, "User updated successfully", userService.updateUser(id, request), "/api/v1/users/" + id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "User deleted successfully", null, "/api/v1/users/" + id));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<ApiResponse<UserResponse>> toggleStatus(@PathVariable Long id, @RequestBody Boolean enabled) {
        return ResponseEntity.ok(new ApiResponse<>(true, "User status updated successfully", userService.toggleStatus(id, enabled), "/api/v1/users/" + id + "/status"));
    }

    @PutMapping("/change-password")
    public ResponseEntity<ApiResponse<UserResponse>> changeOwnPassword(@Valid @RequestBody ChangePasswordRequest request) {
        User current = tz.go.tpa.ict_assets_management.util.CurrentUser.required();
        return ResponseEntity.ok(new ApiResponse<>(true, "Password changed successfully", userService.changePassword(current.getId(), request), "/api/v1/users/change-password"));
    }

    @PutMapping("/{id}/change-password")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<ApiResponse<UserResponse>> changePassword(@PathVariable Long id, @Valid @RequestBody ChangePasswordRequest request) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Password changed successfully", userService.changePassword(id, request), "/api/v1/users/" + id + "/change-password"));
    }

    @GetMapping("/{id}/roles")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<ApiResponse<List<Role>>> getUserRoles(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponse<>(true, "User roles retrieved successfully", userService.getUserRoles(id), "/api/v1/users/" + id + "/roles"));
    }
}
