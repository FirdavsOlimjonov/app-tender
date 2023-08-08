package uz.mc.apptender.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.mc.apptender.modules.Stroy;
import uz.mc.apptender.modules.enums.RoleEnum;

import java.util.Optional;

public interface StroyRepository extends JpaRepository<Stroy,Integer> {
    @Query(value = "select max(tender_id) from stroy", nativeQuery = true)
    Integer findMaxTenderId();

    Optional<Stroy> findFirstByLotId(long lotId);

    Optional<Stroy> findFirstByLotIdAndDeletedIsFalse(long lotId);

    @Modifying
    @Query(nativeQuery = true, value = """
                update tender_offeror set deleted = true where lot_id = :lot_id and deleted is false;
                
                update tender_customer set deleted = true where lot_id = :lot_id and deleted is false;
                
                update svod_resurs_offeror set deleted = true where stroy_id = :stroy_id and deleted is false;
                
                update svod_resurs set deleted = true where stroy_id = :stroy_id and deleted is false;
                
                update smeta_itog set deleted = true where stroy_id = :stroy_id and deleted is false;
                
                update smeta set deleted = true where stroy_id = :stroy_id and deleted is false;
                
                update object set deleted = true where stroy_id = :stroy_id and deleted is false;
                
                update stroy set deleted = true where id = :stroy_id and deleted is false;
            """)
    void updateAllTableToDeletedIsTrue(Integer stroy_id, Long lot_id);
}
