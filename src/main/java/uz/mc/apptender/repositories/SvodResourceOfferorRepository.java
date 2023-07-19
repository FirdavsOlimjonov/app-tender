package uz.mc.apptender.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.mc.apptender.modules.Stroy;
import uz.mc.apptender.modules.SvodResurs;
import uz.mc.apptender.modules.SvodResursOfferor;

import java.util.List;

public interface SvodResourceOfferorRepository extends JpaRepository<SvodResursOfferor, Long> {
    List<SvodResursOfferor> findAllByStroy(Stroy stroy);
}
