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

    Optional<Stroy> findFirstByUserIdAndLotId(long userId, long lotId);

    Optional<Stroy> findFirstByLotIdAndRoleAndUserIdAndDeletedIsFalse(long lotId, RoleEnum role, long userId);

    @Modifying
    @Query(value =
                    "UPDATE tender_customer SET deleted = true WHERE role = :role and user_id = :userId and deleted = false and smeta_id in " +
                            "(select id from smeta where role = :role and user_id = :userId and deleted = false and object_id in (select id from object where role = :role and user_id = :userId and stroy_id = :stroyId and deleted = false));\n" +
                    "UPDATE smeta SET deleted = true WHERE role = :role and user_id = :userId and deleted = false and object_id in  (select id from object where role = :role and user_id = :userId and stroy_id = :stroyId and deleted = false);\n" +
                    "UPDATE object SET deleted = true WHERE role = :role and user_id = :userId and stroy_id = :stroyId and deleted = false;", nativeQuery = true)
    void deleteAllByUserAndRole(@Param("role") String role, @Param("userId")long userId, @Param("stroyId")Integer stroyId);
}
