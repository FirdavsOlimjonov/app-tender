package uz.mc.apptender.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uz.mc.apptender.modules.Stroy;
import uz.mc.apptender.modules.SvodResurs;
import uz.mc.apptender.modules.SvodResursOfferor;

import java.util.List;
import java.util.Optional;

public interface SvodResourceOfferorRepository extends JpaRepository<SvodResursOfferor, Long> {
    @Query(nativeQuery = true, value = "select * from svod_resurs_offeror where stroy_id = :stroy_id and deleted is false;")
    List<SvodResursOfferor> findAllByStroy_Id(Integer stroy_id);

    boolean existsByUserIdAndStroy_Id(long userId, Integer stroy_id);

}
