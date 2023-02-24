package uz.mc.apptender.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.mc.apptender.modules.Stroy;

import java.util.Optional;

public interface StroyRepository extends JpaRepository<Stroy,Integer> {
    Optional<Stroy> findTopByOrderByCreatedAtDesc();

    Optional<Stroy> findByStrNameEqualsIgnoreCase(String strName);

    boolean existsByStrNameEqualsIgnoreCase(String strName);
}
