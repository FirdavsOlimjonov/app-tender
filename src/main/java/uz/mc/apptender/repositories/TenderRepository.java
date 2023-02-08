package uz.mc.apptender.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.mc.apptender.modules.Tender;

import java.util.Optional;

public interface TenderRepository extends JpaRepository<Tender,Integer> {

    Optional<Tender> findTopByOrderByCreatedAtDesc();
}
