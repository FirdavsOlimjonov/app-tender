package uz.mc.apptender.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uz.mc.apptender.modules.Stroy;

import java.util.Optional;

public interface StroyRepository extends JpaRepository<Stroy,Integer> {
    @Query(value = "select max(order_index) from stroy", nativeQuery = true)
    Integer findMaxTenderId();

    Optional<Stroy> findByStrNameEqualsIgnoreCase(String strName);

    boolean existsByStrNameEqualsIgnoreCase(String strName);
}
