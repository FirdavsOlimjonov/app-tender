package uz.mc.apptender.component;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import uz.mc.apptender.modules.Role;
import uz.mc.apptender.modules.Tender;
import uz.mc.apptender.modules.User;
import uz.mc.apptender.modules.enums.PermissionEnum;
import uz.mc.apptender.modules.enums.RoleEnum;
import uz.mc.apptender.repositories.RoleRepository;
import uz.mc.apptender.repositories.TenderRepository;
import uz.mc.apptender.repositories.UserRepository;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final TenderRepository tenderRepository;

    @Value("${spring.jpa.hibernate.ddl-auto}")
    private String ddlMode;

    @Value("${app.admin.username}")
    private String adminUsername;

    @Value("${app.admin.password}")
    private String adminPassword;

    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (Objects.equals(ddlMode, "create")) {

            Role role = new Role();
            role.setName(RoleEnum.ADMIN.name());
            role.setDescription("Project owner");
            role.setPermissions(Set.of(PermissionEnum.values()));
            roleRepository.save(role);


            Role roleUser = new Role();
            roleUser.setName(RoleEnum.USER.name());
            roleUser.setDescription("Foydalanuvchui");
            roleUser.setPermissions(Set.of(PermissionEnum.GET_PROJECTS));
            roleRepository.save(roleUser);

            User admin = new User(adminUsername,passwordEncoder.encode(adminPassword));
            admin.setRole(role);
            admin.setEnabled(true);
            userRepository.save(admin);
        }

    }

}
