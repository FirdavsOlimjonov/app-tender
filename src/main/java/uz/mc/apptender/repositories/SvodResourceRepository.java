package uz.mc.apptender.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uz.mc.apptender.modules.Stroy;
import uz.mc.apptender.modules.SvodResurs;

import java.util.List;

public interface SvodResourceRepository extends JpaRepository<SvodResurs, Long> {

    @Query(nativeQuery = true, value = "select * from svod_resurs where stroy_id = :stroy_id ;")
    List<SvodResurs> findAllByStroy(Integer stroy_id);

    @Query(nativeQuery = true, value = """
            select * from svod_resurs sv where stroy_id = (select id from stroy where lot_id = :lotId) and tip = :tip and deleted is false order by num;
            """)
    List<SvodResurs> findAllByStroy_LotId(long lotId, Integer tip);
}
