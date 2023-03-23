package uz.mc.apptender.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.mc.apptender.modules.Smeta;
import uz.mc.apptender.modules.TenderOfferor;
import uz.mc.apptender.modules.enums.RoleEnum;

import java.util.Optional;

public interface TenderOfferorRepository extends JpaRepository<TenderOfferor,Integer> {

    Optional<TenderOfferor> findBySmIdAndUserId(Integer smId, long userId);
}
