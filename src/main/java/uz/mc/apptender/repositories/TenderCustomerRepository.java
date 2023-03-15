package uz.mc.apptender.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.mc.apptender.modules.Smeta;
import uz.mc.apptender.modules.TenderCustomer;
import uz.mc.apptender.modules.enums.RoleEnum;

import java.util.Optional;

public interface TenderCustomerRepository extends JpaRepository<TenderCustomer,Integer> {

    Optional<TenderCustomer> findFirstByUserIdAndRoleAndSmeta(long userId, RoleEnum role, Smeta smeta);
}
