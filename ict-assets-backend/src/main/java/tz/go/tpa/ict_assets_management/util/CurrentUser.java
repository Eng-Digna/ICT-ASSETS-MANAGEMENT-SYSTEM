package tz.go.tpa.ict_assets_management.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import tz.go.tpa.ict_assets_management.entity.User;

public final class CurrentUser {
    private CurrentUser() {
    }

    public static User required() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User user) {
            return user;
        }
        throw new IllegalStateException("An authenticated user is required");
    }
}