package uz.mc.apptender.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.mc.apptender.modules.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByUsernameEqualsIgnoreCase(String username);
    Boolean existsByUsername(String username);
    Optional<User> findUserByUsernameAndPassword(String username, String password);

    boolean existsByUsernameAndPassword(String username, String password);
}
