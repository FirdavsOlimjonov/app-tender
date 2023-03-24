package uz.mc.apptender.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.mc.apptender.modules.Object;
import uz.mc.apptender.modules.Smeta;
import uz.mc.apptender.modules.enums.RoleEnum;

import java.util.Optional;

public interface SmetaRepository extends JpaRepository<Smeta,Integer> {

    Optional<Smeta> findFirstByIdAndObject_Id(Integer id, Integer object_id);
}
