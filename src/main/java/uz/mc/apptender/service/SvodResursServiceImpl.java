package uz.mc.apptender.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.mc.apptender.exeptions.RestException;
import uz.mc.apptender.modules.Object;
import uz.mc.apptender.modules.*;
import uz.mc.apptender.modules.enums.RoleEnum;
import uz.mc.apptender.payload.ApiResult;
import uz.mc.apptender.payload.AuthLotDTO;
import uz.mc.apptender.payload.SvodResursDAO;
import uz.mc.apptender.payload.SvodResursUpdate;
import uz.mc.apptender.repositories.*;
import uz.mc.apptender.utils.Utils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class SvodResursServiceImpl implements SvodResursService {
    private final SvodResourceOfferorRepository svodResourceOfferorRepository;
    private final SvodResourceRepository svodResourceRepository;
    private final TenderOfferorRepository tenderOfferorRepository;
    private final TenderCustomerRepository tenderCustomerRepository;
    private final StroyRepository stroyRepository;
    private final ObjectRepository objectRepository;
    private final SmetaRepository smetaRepository;
    private final Utils utils;

    @Override
    public ApiResult<List<SvodResursDAO>> get(long lot_id, long inn) {
        AuthLotDTO authLotDTO = utils.sendToGetRoleOfLot(inn, lot_id);
        RoleEnum role = authLotDTO.getRole().toUpperCase().trim().equals(RoleEnum.CUSTOMER.name()) ? RoleEnum.CUSTOMER : RoleEnum.OFFEROR;

        log.info(String.format("Request send for get role and auth: role = %s, userId = %s", authLotDTO.getRole(), authLotDTO.getUserId()));

        Stroy stroy = stroyRepository.findFirstByLotId(lot_id).orElseThrow(
                () -> RestException.restThrow("This lot not found!", HttpStatus.NOT_FOUND));

        log.info(String.format("Stroy name: %s, inn: %d,  lot_id = %d", stroy.getStrName(), inn, lot_id));

        List<SvodResursDAO> res;

        //agar offeror bolsa price = 0 boladi
        if (Objects.equals(role, RoleEnum.OFFEROR)) res = getSvodResursDAOS(stroy, authLotDTO);
        else res = svodResourceRepository.findAllByStroy(stroy.getId())
                .stream()
                .map(this::mapSvodResursDao)
                .toList();

        return ApiResult.successResponse(res, inn, lot_id);
    }

    private List<SvodResursDAO> getSvodResursDAOS(Stroy stroy, AuthLotDTO authLotDTO) {
        //agar oldin yaratilgan bolsa id bilan qaytarish aks holda id bermaslik
        if (svodResourceOfferorRepository.existsByUserIdAndStroy_Id(authLotDTO.getUserId(), stroy.getId()))
            return svodResourceOfferorRepository.findAllByStroy_Id(stroy.getId())
                    .stream()
                    .map(this::mapSvodResursDaoWithoutPrices)
                    .toList();

        return svodResourceRepository.findAllByStroy(stroy.getId())
                .stream()
                .map(this::mapSvodResursDaoWithoutPrices)
                .toList();
    }

    @Override
    @Transactional
    public ApiResult<?> update(SvodResursUpdate svodResursUpdate) {
        Long inn = svodResursUpdate.getInn();
        Long lotId = svodResursUpdate.getLot_id();

        AuthLotDTO authLotDTO = utils.sendToGetRoleOfLot(inn, lotId);
        RoleEnum role = authLotDTO.getRole().toUpperCase().trim().equals(RoleEnum.CUSTOMER.name()) ? RoleEnum.CUSTOMER : RoleEnum.OFFEROR;

        log.info(String.format("Request send for get role and auth: role = %s, userId = %s", authLotDTO.getRole(), authLotDTO.getUserId()));

        //agar customer kelsa uni qaytarvorish
        if (Objects.equals(role, RoleEnum.CUSTOMER))
            throw RestException.restThrow("Customer cannot update smeta from this method!");

        //AGAR TENDER ELONGA CHIQMAGAN BO'LSA UNi YARATA OLMAYDI(UNGA O'ZGARTIRISH KIRITA OLMAYDI) OFFEROR
        if (!authLotDTO.isOfferorCanChange())
            throw RestException.restThrow("Offerror cannot add or change price and summa. Because tender has already published!");

        Stroy stroy = stroyRepository.findFirstByLotId(lotId).orElseThrow(
                () -> RestException.restThrow("This lot not found!", HttpStatus.NOT_FOUND));

        log.info(String.format("Stroy name: %s, inn: %d,  lot_id = %d", stroy.getStrName(), inn, lotId));

        //agar oldin yaratilmagan bolsa yaratib keyin update qilish uchun
        if (!tenderOfferorRepository.existsByLotIdAndUserId(lotId, authLotDTO.getUserId()))
            createSmetaOfferor(lotId, authLotDTO);

        updateSmetaOfferor(svodResursUpdate, stroy, authLotDTO);

        return ApiResult.successResponse(inn, lotId);
    }

    private void createSmetaOfferor(long lotId, AuthLotDTO authLotDTO) {
        List<TenderOfferor> tenderOfferorListForSave = new ArrayList<>();

        List<Object> objectAllByLotId = objectRepository.findAllByStroy_LotId(lotId);
        for (Object object : objectAllByLotId) {

            List<Smeta> smetaAllByObjectId = smetaRepository.findAllByObject_Id(object.getId());
            for (Smeta smeta : smetaAllByObjectId) {

                List<TenderCustomer> tenderAllBySmetaId = tenderCustomerRepository.findAllBySmeta_Id(smeta.getId());
                for (TenderCustomer tenderCustomer : tenderAllBySmetaId) {

                    TenderOfferor tenderOfferor = getTenderOfferor(lotId, authLotDTO, smeta, tenderCustomer);

                    //agar bolalari bolsa ularni ham create qilish kerak
                    if (tenderCustomer.getRowType() == 0) {
                        TenderOfferor tenderOfferorSaved = tenderOfferorRepository.save(tenderOfferor);

                        List<TenderCustomer> tenderCustomerListChild
                                = tenderCustomerRepository.findAllByParentId(tenderCustomer.getSmId());

                        for (TenderCustomer child : tenderCustomerListChild)
                            tenderOfferorListForSave.add(getTenderOfferorWithParent(lotId, authLotDTO, smeta, child, tenderOfferorSaved));

                    }
                    //Agar rowtype != 0 bolsa child uchun idsi kerak bo'lmaydi shu uchun bu entitylarni keyinroq save qilsak boladi
                    else tenderOfferorListForSave.add(tenderOfferor);
                }

                //todo Smeta Itogni ham yozish kerak?
            }
        }

        tenderOfferorRepository.saveAll(tenderOfferorListForSave);
    }


    private void updateSmetaOfferor(SvodResursUpdate svodResursUpdate, Stroy stroy, AuthLotDTO authLotDTO) {
        List<SvodResursDAO> svodResursList = svodResursUpdate.getSvod_resurs();
        List<TenderOfferor> tenderOfferorList = new ArrayList<>();

        List<SvodResursOfferor> svodResursOfferors = new ArrayList<>();

        for (SvodResursDAO svodResursDAO : svodResursList) {
            //agar oldinyaratilgan bolsa oshani qoshib qaytaradi aks holsa yangi yaratib qaytaradi
            mapSvodResursOfferor(svodResursDAO, stroy, svodResursOfferors, authLotDTO.getUserId());

            //kodr boyicha kodSnk dan olib kelib update qilish uchun
            updateTenderOfferor(svodResursDAO, tenderOfferorList);
        }

        svodResourceOfferorRepository.saveAll(svodResursOfferors);
    }

    private void updateTenderOfferor(SvodResursDAO svodResursDAO, List<TenderOfferor> tenderOfferorList) {
        String kodr = svodResursDAO.getKodr();

        //todo aniq yechim yoq bosh bolsa kod
        if (!kodr.isBlank()) {
            List<TenderOfferor> allByKodSnk = tenderOfferorRepository.findAllByKodSnk(kodr);

            for (TenderOfferor tenderOfferor : allByKodSnk) {
                double summa = tenderOfferor.getRashod() * svodResursDAO.getPrice().doubleValue();

                tenderOfferor.setPrice(svodResursDAO.getPrice());
                tenderOfferor.setSumma(new BigDecimal(summa));

                tenderOfferorList.add(tenderOfferor);
            }
        }
    }

    private void mapSvodResursOfferor(SvodResursDAO svodResurs, Stroy stroy, List<SvodResursOfferor> svodResursOfferors, long userId) {
        SvodResursOfferor svodResursOfferor;
        if (svodResurs.getId() != null) {
            svodResursOfferor = svodResourceOfferorRepository.findById(svodResurs.getId())
                    .orElseThrow(() -> RestException.restThrow("SvodResurs' id is not correct!"));

            svodResursOfferor.setSumma(svodResurs.getSumma());
            svodResursOfferor.setPrice(svodResurs.getPrice());
            svodResursOfferor.setKol(svodResurs.getKol());

            svodResursOfferors.add(svodResursOfferor);
        }
        else {
            svodResursOfferor = getSvodResursOfferor(svodResurs, stroy, userId);
            svodResursOfferors.add(svodResursOfferor);
        }
    }


    private static TenderOfferor getTenderOfferor(long lotId, AuthLotDTO authLotDTO, Smeta smeta, TenderCustomer tenderCustomer) {
        return TenderOfferor.builder()
                .price(new BigDecimal(0))
                .summa(new BigDecimal(0))
                .rashod(tenderCustomer.getRashod())
                .norma(tenderCustomer.getNorma())
                .name(tenderCustomer.getName())
                .kodSnk(tenderCustomer.getKodSnk())
                .rowType(tenderCustomer.getRowType())
                .edIsm(tenderCustomer.getEdIsm())
                .num(tenderCustomer.getNum())
                .smeta(smeta)
                .lotId(lotId)
                .opred(tenderCustomer.getOpred())
                .userId(authLotDTO.getUserId())
                .build();
    }

    private static TenderOfferor getTenderOfferorWithParent(long lotId, AuthLotDTO authLotDTO, Smeta smeta, TenderCustomer tenderCustomer, TenderOfferor parent) {
        return TenderOfferor.builder()
                .price(new BigDecimal(0))
                .summa(new BigDecimal(0))
                .rashod(tenderCustomer.getRashod())
                .norma(tenderCustomer.getNorma())
                .name(tenderCustomer.getName())
                .kodSnk(tenderCustomer.getKodSnk())
                .rowType(tenderCustomer.getRowType())
                .edIsm(tenderCustomer.getEdIsm())
                .num(tenderCustomer.getNum())
                .smeta(smeta)
                .lotId(lotId)
                .opred(tenderCustomer.getOpred())
                .userId(authLotDTO.getUserId())
                .parentId(parent.getSmId())
                .build();
    }

    private SvodResursDAO mapSvodResursDaoWithoutPrices(SvodResurs svodResurs) {
        return new SvodResursDAO(
                null,
                svodResurs.getNum(),
                svodResurs.getKodv(),
                svodResurs.getTip(),
                svodResurs.getKodr(),
                svodResurs.getKodm(),
                svodResurs.getKodiName(),
                svodResurs.getName(),
                svodResurs.getKol(),
                new BigDecimal(0),
                new BigDecimal(0)
        );
    }

    private SvodResursDAO mapSvodResursDaoWithoutPrices(SvodResursOfferor svodResurs) {
        return new SvodResursDAO(
                svodResurs.getId(),
                svodResurs.getNum(),
                svodResurs.getKodv(),
                svodResurs.getTip(),
                svodResurs.getKodr(),
                svodResurs.getKodm(),
                svodResurs.getKodiName(),
                svodResurs.getName(),
                svodResurs.getKol(),
                new BigDecimal(0),
                new BigDecimal(0)
        );
    }

    private SvodResursDAO mapSvodResursDao(SvodResurs svodResurs) {
        return new SvodResursDAO(
                svodResurs.getId(),
                svodResurs.getNum(),
                svodResurs.getKodv(),
                svodResurs.getTip(),
                svodResurs.getKodr(),
                svodResurs.getKodm(),
                svodResurs.getKodiName(),
                svodResurs.getName(),
                svodResurs.getKol(),
                svodResurs.getPrice(),
                svodResurs.getSumma()
        );
    }

    private static SvodResursOfferor getSvodResursOfferor(SvodResursDAO svodResurs, Stroy stroy, long userId) {
        return new SvodResursOfferor(
                svodResurs.getNum(),
                svodResurs.getKodv(),
                svodResurs.getTip(),
                svodResurs.getKodr(),
                svodResurs.getKodm(),
                svodResurs.getKodiName(),
                svodResurs.getName(),
                svodResurs.getKol(),
                svodResurs.getPrice(),
                svodResurs.getSumma(),
                stroy, userId
        );
    }
}
