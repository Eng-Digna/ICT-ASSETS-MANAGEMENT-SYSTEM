package tz.go.tpa.ict_assets_management.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import tz.go.tpa.ict_assets_management.entity.Role;
import tz.go.tpa.ict_assets_management.entity.RoleName;
import tz.go.tpa.ict_assets_management.entity.User;
import tz.go.tpa.ict_assets_management.repository.RoleRepository;
import tz.go.tpa.ict_assets_management.repository.UserRepository;

@Component
@ConditionalOnProperty(name = "app.seed-admin", havingValue = "true")
public class DevelopmentDataInitializer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public DevelopmentDataInitializer(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.existsByUsername("admin")) return;

        Role role = roleRepository.findByName(RoleName.ADMINISTRATOR)
                .orElseGet(() -> roleRepository.save(new Role(RoleName.ADMINISTRATOR, "System administrator")));
        User user = new User();
        user.setUsername("admin");
        user.setEmail("admin@tpa.go.tz");
        user.setFirstName("System");
        user.setLastName("Administrator");
        user.setPasswordHash(passwordEncoder.encode("Admin@123"));
        user.setRoles(java.util.Set.of(role));
        userRepository.save(user);
    }
}