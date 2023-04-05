package uz.mc.apptender.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.mc.apptender.modules.SmetaItog;

import java.util.Optional;

public interface SmetaItogRepository extends JpaRepository<SmetaItog, Long>{
    Optional<SmetaItog> findBySmeta_Id(Long smeta_id);
}
