package uz.mc.apptender.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
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

        List<SvodResursDAO> res;

        if (Objects.equals(role, RoleEnum.OFFEROR))
            res = svodResourceRepository.findAllByStroy_LotId(lot_id)
                    .stream()
                    .map(this::mapSvodResursDaoWithoutPrices)
                    .toList();
        else
            res = svodResourceRepository.findAllByStroy_LotId(lot_id)
                    .stream()
                    .map(this::mapSvodResursDao)
                    .toList();

        return ApiResult.successResponse(res);
    }

    @Override
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

        return updateSmetaOfferor(svodResursUpdate, stroy);
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
                                = tenderCustomerRepository.findAllBySmeta_IdAndParentId(smeta.getId(), tenderCustomer.getSmId());

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


    private ApiResult<?> updateSmetaOfferor(SvodResursUpdate svodResursUpdate, Stroy stroy) {
        List<SvodResursDAO> svodResursList = svodResursUpdate.getSvod_resurs();
        List<SvodResursOfferor> svodResursOfferors = new ArrayList<>();
        List<TenderOfferor> tenderOfferorList = new ArrayList<>();

        for (SvodResursDAO svodResursDAO : svodResursList) {
            svodResursOfferors.add(mapSvodResursOfferor(svodResursDAO, stroy));

            //kodr boyicha kodSnk dan olib kelib update qilish uchun
            updateTenderOfferor(svodResursDAO , tenderOfferorList);
        }

        svodResourceOfferorRepository.saveAll(svodResursOfferors);

        return ApiResult.successResponse();
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

    private SvodResursOfferor mapSvodResursOfferor(SvodResursDAO svodResurs, Stroy stroy) {
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
                stroy
        );
    }
}
