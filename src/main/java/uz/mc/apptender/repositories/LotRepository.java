package uz.mc.apptender.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.mc.apptender.modules.Lot;

import java.util.Optional;

public interface LotRepository extends JpaRepository<Lot,Integer> {

    Optional<Lot> findTopByOrderByCreatedAtDesc();
}
