package uz.mc.apptender.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.mc.apptender.modules.Smeta;
import uz.mc.apptender.modules.TenderOfferor;
import uz.mc.apptender.modules.enums.RoleEnum;

import java.util.List;
import java.util.Optional;

public interface TenderOfferorRepository extends JpaRepository<TenderOfferor,Long> {

    Optional<TenderOfferor> findBySmIdAndUserIdAndSmeta_Id(Long smId, long userId, Long smeta_id);

    boolean existsByLotId(long lotId);

    List<TenderOfferor> findAllBySmeta_idAndUserId(Long smeta_id, long userId);

    Optional<TenderOfferor> findBySmIdAndUserId(Long smId, long userId);

    boolean existsByLotIdAndUserId(long lotId, long userId);

    List<TenderOfferor> findAllByParentId(Long smId);

    List<TenderOfferor> findAllByKodSnk(String kodr);
}
