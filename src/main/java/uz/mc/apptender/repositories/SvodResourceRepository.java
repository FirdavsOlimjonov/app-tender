package uz.mc.apptender.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.mc.apptender.modules.Stroy;
import uz.mc.apptender.modules.SvodResurs;

import java.util.List;

public interface SvodResourceRepository extends JpaRepository<SvodResurs, Long> {

    List<SvodResurs> findAllByStroy(Stroy stroy);
}
