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

    Optional<TenderCustomer> findBySmId(Long smId);

    Optional<TenderCustomer> findBySmIdAndSmeta_Id(Long smId, Long smeta_id);

    Optional<TenderCustomer> findBySmIdAndParentId(Long smId, Long parentId);
    List<TenderCustomer> findAllBySmeta_IdAndParentId(Long smeta_id, Long parent_id);

    List<TenderCustomer> findAllBySmeta_IdAndUserId(Long smeta_id, Long userId);

    List<TenderCustomer> findAllBySmeta_Id(Long smeta_id);

    List<TenderCustomer> findAllBySmeta_IdAndParentIdIsNull(Long smeta_id);

    @Query(nativeQuery = true, value = """
            SELECT DISTINCT ON (kod_snk) kod_snk, sm_id as id, name, ed_ism, norma, price, summa
            FROM tender_customer\s
                where parent_id is null and smeta_id = :smeta_id\s
            ORDER BY kod_snk,sm_id;
            """)
    List<TenderProjection> findAllBySmeta_IdOOrderByKodSnkOrSmId(Long smeta_id);

    List<TenderCustomer> findAllByParentId(Long parentId);

    @Query(nativeQuery = true, value = """
            select coalesce(sum(rashod), 0) from tender_customer
            where lot_id = :lot_id and kod_snk = :kodr ;
            """)
    double sumRashod(Long lot_id, String kodr);
}
