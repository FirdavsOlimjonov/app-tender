package uz.mc.apptender.component;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    @Value("${spring.jpa.hibernate.ddl-auto}")
    private String ddlMode;

//    @Value("${app.admin.username}")
//    private String adminUsername;
//
//    @Value("${app.admin.password}")
//    private String adminPassword;

    @Override
    public void run(String... args) {
//        if (Objects.equals(ddlMode, "create")) {
//
//        }

    }

}
