package uz.mc.apptender.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.mc.apptender.modules.Object;
import uz.mc.apptender.modules.Stroy;
import uz.mc.apptender.modules.enums.RoleEnum;

import java.util.Optional;

public interface ObjectRepository extends JpaRepository<Object,Integer> {

    Optional<Object> findFirstByIdAndStroy_Id(Integer id, Integer stroy_id);
}
