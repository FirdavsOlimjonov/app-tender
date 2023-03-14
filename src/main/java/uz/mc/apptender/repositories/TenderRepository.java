package uz.mc.apptender.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.mc.apptender.modules.TenderCustomer;

import java.util.Optional;

public interface TenderRepository extends JpaRepository<TenderCustomer,Integer> {

    Optional<TenderCustomer> findTopByOrderByCreatedAtDesc();
}
