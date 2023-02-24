//package uz.mc.apptender.repositories;
//
//import org.springframework.data.jpa.repository.JpaRepository;
//
//import java.util.Optional;
//import java.util.UUID;
//
//public interface UserRepository extends JpaRepository<Employee, UUID> {
//    Optional<Employee> findByUsernameEqualsIgnoreCase(String username);
//    Boolean existsByUsername(String username);
//    Optional<Employee> findUserByUsernameAndPassword(String username, String password);
//
//    boolean existsByUsernameAndPassword(String username, String password);
//}
