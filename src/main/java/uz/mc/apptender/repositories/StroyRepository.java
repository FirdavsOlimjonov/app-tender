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


    @Query()
    Optional<Stroy> findFirstByLotIdAndRoleAndUserId(long lotId, RoleEnum role, long userId);

    @Modifying
    @Query(value =
            "DELETE FROM tender_customer WHERE role = :role and user_id = :userId and smeta_id in " +
                    "(select id from smeta where role = :role and user_id = :userId and object_id in " +
                        "(select id from object where role = :role and user_id = :userId and stroy_id = :stroyId));\n" +
            "DELETE FROM smeta WHERE role = :role and user_id = :userId and object_id in " +
                    "(select id from object where role = :role and user_id = :userId and stroy_id = :stroyId);\n" +
            "DELETE FROM object WHERE role = :role and user_id = :userId and stroy_id = :stroyId ;"
            , nativeQuery = true)
    void deleteAllByUserAndRole(@Param("role") String role, @Param("userId")long userId, @Param("stroyId")Integer stroyId);
}
