package uz.mc.apptender.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.mc.apptender.modules.Smeta;
import uz.mc.apptender.modules.TenderCustomer;
import uz.mc.apptender.modules.enums.RoleEnum;

import java.util.List;
import java.util.Optional;

public interface TenderCustomerRepository extends JpaRepository<TenderCustomer,Long> {

    Optional<TenderCustomer> findBySmId(Long smId);

    Optional<TenderCustomer> findBySmIdAndSmeta_Id(Long smId, Long smeta_id);

    Optional<TenderCustomer> findBySmIdAndParentId(Long smId, Long parentId);

    List<TenderCustomer> findAllBySmeta_IdAndUserId(Long smeta_id, Long userId);
    List<TenderCustomer> findAllBySmeta_Id(Long smeta_id);

    List<TenderCustomer> findAllByParentId(Long parentId);
}
