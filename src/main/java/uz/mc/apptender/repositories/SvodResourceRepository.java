package uz.mc.apptender.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uz.mc.apptender.modules.Stroy;
import uz.mc.apptender.modules.SvodResurs;

import java.util.List;

public interface SvodResourceRepository extends JpaRepository<SvodResurs, Long> {

    List<SvodResurs> findAllByStroy(Stroy stroy);

    @Query(nativeQuery = true, value = """
            select * from svod_resurs sv where stroy_id = (select id from stroy where lot_id = :lotId) order by num;
            """)
    List<SvodResurs> findAllByStroy_LotId(long lotId);
}
