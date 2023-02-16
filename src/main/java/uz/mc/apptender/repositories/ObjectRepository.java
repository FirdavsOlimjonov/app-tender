package uz.mc.apptender.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.mc.apptender.modules.Object;
import uz.mc.apptender.modules.Tender;

import java.util.Optional;

public interface ObjectRepository extends JpaRepository<Object,Integer> {
    Optional<Object> findByObNameEqualsIgnoreCase(String obName);
}
