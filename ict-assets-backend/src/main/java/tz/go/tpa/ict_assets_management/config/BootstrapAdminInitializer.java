package tz.go.tpa.ict_assets_management.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tz.go.tpa.ict_assets_management.entity.Role;
import tz.go.tpa.ict_assets_management.entity.RoleName;
import tz.go.tpa.ict_assets_management.entity.User;
import tz.go.tpa.ict_assets_management.repository.RoleRepository;
import tz.go.tpa.ict_assets_management.repository.UserRepository;

@Component
public class BootstrapAdminInitializer implements ApplicationRunner {
    private final UserRepository users; private final RoleRepository roles; private final PasswordEncoder passwords;
    private final String username, email, password, firstName, lastName;
    public BootstrapAdminInitializer(UserRepository users, RoleRepository roles, PasswordEncoder passwords,
            @Value("${bootstrap.admin.username:}") String username, @Value("${bootstrap.admin.email:}") String email,
            @Value("${bootstrap.admin.password:}") String password, @Value("${bootstrap.admin.first-name:System}") String firstName,
            @Value("${bootstrap.admin.last-name:Administrator}") String lastName) {
        this.users = users; this.roles = roles; this.passwords = passwords; this.username = username; this.email = email;
        this.password = password; this.firstName = firstName; this.lastName = lastName;
    }
    @Override @Transactional public void run(ApplicationArguments args) {
        Role admin = roles.findByName(RoleName.ADMINISTRATOR).orElseGet(() -> roles.save(new Role(RoleName.ADMINISTRATOR, "System administrator")));
        roles.findByName(RoleName.REGISTRAR).orElseGet(() -> roles.save(new Role(RoleName.REGISTRAR, "Station-level ICT officer")));
        if (users.count() != 0 || username.isBlank() || email.isBlank() || password.isBlank()) return;
        User user = new User(); user.setUsername(username); user.setEmail(email); user.setPasswordHash(passwords.encode(password));
        user.setFirstName(firstName); user.setLastName(lastName); user.setEnabled(true); user.getRoles().add(admin); users.save(user);
    }
}
