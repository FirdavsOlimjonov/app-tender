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

    Optional<Smeta> findFirstByIdAndObject_Id(Long id, Integer object_id);

//    @Query(value = """
//            select s.id, s.sm_num, s.sm_name, s.user_id from smeta s where s.object_id = :id
//            """, nativeQuery = true)
    List<Smeta> findAllByObject_Id(Integer id);

    Smeta findFirstById(Long id);
}
