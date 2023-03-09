package uz.mc.apptender.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.mc.apptender.modules.Tender;
import uz.mc.apptender.modules.TenderOfferor;

import java.util.Optional;

public interface TenderOfferorRepository extends JpaRepository<TenderOfferor,Integer> {

    Optional<TenderOfferor> findTopByOrderByCreatedAtDesc();
}
