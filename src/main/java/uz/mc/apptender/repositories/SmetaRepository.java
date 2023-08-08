package uz.mc.apptender.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uz.mc.apptender.modules.Object;
import uz.mc.apptender.modules.Smeta;
import uz.mc.apptender.modules.enums.RoleEnum;
import uz.mc.apptender.payload.projections.SmetaGetProjection;

import java.util.List;
import java.util.Optional;

public interface SmetaRepository extends JpaRepository<Smeta, Long> {
    List<Smeta> findAllByObject_Id(Integer id);

}
