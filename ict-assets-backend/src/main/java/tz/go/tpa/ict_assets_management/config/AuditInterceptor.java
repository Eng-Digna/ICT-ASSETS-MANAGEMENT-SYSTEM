package tz.go.tpa.ict_assets_management.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import tz.go.tpa.ict_assets_management.entity.AuditLog;
import tz.go.tpa.ict_assets_management.repository.AuditLogRepository;
import tz.go.tpa.ict_assets_management.repository.UserRepository;

@Component
public class AuditInterceptor implements HandlerInterceptor {

    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;

    public AuditInterceptor(AuditLogRepository auditLogRepository, UserRepository userRepository) {
        this.auditLogRepository = auditLogRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        String method = request.getMethod();
        if ("GET".equalsIgnoreCase(method) || "OPTIONS".equalsIgnoreCase(method)) {
            return;
        }

        AuditLog auditLog = new AuditLog();
        String action = switch (method.toUpperCase()) {
            case "POST" -> "CREATE";
            case "PUT", "PATCH" -> "UPDATE";
            case "DELETE" -> "DELETE";
            default -> method;
        };

        auditLog.setAction(action);
        auditLog.setHttpMethod(method);
        auditLog.setEndpoint(request.getRequestURI());
        auditLog.setIpAddress(request.getRemoteAddr());
        auditLog.setUserAgent(request.getHeader("User-Agent"));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !authentication.getName().equals("anonymousUser")) {
            String username = authentication.getName();
            auditLog.setUsername(username);
            userRepository.findByUsername(username).ifPresent(user -> auditLog.setUserId(user.getId()));
        } else {
            auditLog.setUsername("System/Anonymous");
        }

        String[] parts = request.getRequestURI().split("/");
        if (parts.length > 3) {
            String resource = parts[3];
            auditLog.setResourceType(resource.substring(0, 1).toUpperCase() + resource.substring(1));
        } else {
            auditLog.setResourceType("Unknown");
        }

        auditLog.setSuccess(ex == null && response.getStatus() < 400);
        if (ex != null) {
            auditLog.setFailureReason(ex.getMessage());
        } else if (response.getStatus() >= 400) {
            auditLog.setFailureReason("HTTP " + response.getStatus());
        }

        auditLogRepository.save(auditLog);
    }
}
