package uz.mc.apptender.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uz.mc.apptender.modules.Smeta;
import uz.mc.apptender.modules.TenderCustomer;
import uz.mc.apptender.modules.enums.RoleEnum;
import uz.mc.apptender.payload.projections.TenderProjection;

import java.util.List;
import java.util.Optional;

public interface TenderCustomerRepository extends JpaRepository<TenderCustomer,Long> {

    List<TenderCustomer> findAllBySmeta_IdAndUserId(Long smeta_id, Long userId);

    List<TenderCustomer> findAllBySmeta_Id(Long smeta_id);

    List<TenderCustomer> findAllByParentId(Long parentId);

    @Query(nativeQuery = true, value = """
            select coalesce(sum(rashod), 0) from tender_customer
            where lot_id = :lot_id and kod_snk = :kodr and deleted is false;
            """)
    double sumRashod(Long lot_id, String kodr);
}
