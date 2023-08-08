package uz.mc.apptender.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import uz.mc.apptender.exeptions.RestException;
import uz.mc.apptender.modules.Object;
import uz.mc.apptender.modules.*;
import uz.mc.apptender.modules.enums.RoleEnum;
import uz.mc.apptender.payload.Error;
import uz.mc.apptender.payload.*;
import uz.mc.apptender.repositories.*;
import uz.mc.apptender.utils.Utils;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class TenderServiceImpl implements TenderService {
    private final TenderCustomerRepository tenderCustomerRepository;
    private final TenderOfferorRepository tenderOfferorRepository;
    private final StroyRepository stroyRepository;
    private final ObjectRepository objectRepository;
    private final SmetaRepository smetaRepository;
    private final SmetaItogRepository smetaItogRepository;
    private final SvodResourceRepository svodResourceRepository;
    private final SvodResourceOfferorRepository svodResourceOfferorRepository;
    private final Utils utils;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${app.rest-template.username}")
    private String username;
    @Value("${app.rest-template.password}")
    private String password;
    @Value("${app.rest-template.url-send-sms}")
    private String apiUrl;

    private static final Logger logger = LoggerFactory.getLogger(TenderServiceImpl.class);

    @Override
    @Transactional
    public ApiResult<?> createTender(CreateTenderDTO createTenderDTO) {
        URI uri = UriComponentsBuilder.fromUriString(apiUrl).build().toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBasicAuth(username, password);

        //LOT_ID AND INN SEND TO MUHAMMADALI'S SERVER WITH BASIC AUTH
        logger.info("Send phone number code to get code from Muhammadali's server with inn: " + createTenderDTO.getInn() + " lod_id: " + createTenderDTO.getLotId());
        HttpEntity<CreateTenderDTO> requestEntity = new HttpEntity<>(createTenderDTO, headers);
        TempTenderDTO tempTenderDTO;

        try {
            tempTenderDTO = restTemplate.postForObject(uri, requestEntity, TempTenderDTO.class);
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            e.fillInStackTrace();
            logger.error(e.getMessage() + "  inn: " + createTenderDTO.getInn() + ", lot_id: " + createTenderDTO.getLotId());

            String responseBody = e.getResponseBodyAsString();
            ObjectMapper objectMapper = new ObjectMapper();
            Error error;

            try {
                error = objectMapper.readValue(responseBody, Error.class);
            } catch (JsonProcessingException ex) {
                throw RestException.restThrow(responseBody);
            }

            throw RestException.restThrow(error.toString(), HttpStatus.resolve(error.getCode()));
        }

        // EXTRACT ROLE AND CODE FROM THE RESPONSE DTO OBJECT
        String role = Objects.requireNonNull(tempTenderDTO).getResult().getData().getRole();
        String code = tempTenderDTO.getResult().getData().getCode();

        return ApiResult.successResponse(new TenderAuthDTO(createTenderDTO.getLotId(), createTenderDTO.getInn(), role, code));
    }

    @Override
    @Transactional
    public ApiResult<StroyDTO> add(StroyAddDTO stroyAddDTO) {
        Integer maxTenderId = stroyRepository.findMaxTenderId();
        Integer tenderId = Objects.isNull(maxTenderId) ? 1 : maxTenderId + 1;

        AuthLotDTO authLotDTO = utils.sendToGetRoleOfLot(stroyAddDTO.getInn(), stroyAddDTO.getLotId());
        RoleEnum role = authLotDTO.getRole().toUpperCase().equals(RoleEnum.CUSTOMER.name()) ? RoleEnum.CUSTOMER : RoleEnum.OFFEROR;

        logger.info(String.format("Request send for get role and auth: role = %s, userId = %s", authLotDTO.getRole(), authLotDTO.getUserId()));

        //AGAR OFFEROR BOLSA ERROR QAYTARAMIZ
        if (Objects.equals(role, RoleEnum.OFFEROR))
            throw RestException.restThrow("Offeror cannot add smeta!");

        //AGAR TENDER ELONGA CHIQIB STATUS=2 BOLSA CUSTOMER UNI UPDATE QILA OLMAYDI
        if (!authLotDTO.isCustomerCanChange())
            throw RestException.restThrow("Customer cannot create or update tender details, Check tender status!");

        Stroy stroy = stroyRepository.findFirstByLotIdAndDeletedIsFalse(stroyAddDTO.getLotId()).orElse(new Stroy());

        //AGAR UPDATE QILMOQCHI BOLSA OLDIN  OFERROR VA OZINI TOZALAYMIZ KEYIN QAYTATDAN CREATE QILADI PASTGA TUSHIB
        if (Objects.nonNull((stroy.getId()))) {

            logger.info(String.format("Update deleted is true: lot_id = %s, userId = %s", stroyAddDTO.getLotId(), authLotDTO.getUserId()));

            //hamma tabledagi eski datalarni deleted = true qilish
            stroyRepository.updateAllTableToDeletedIsTrue(stroy.getId(), stroyAddDTO.getLotId());
        }

        if (stroy.getTenderId()!= null)
            tenderId = stroy.getTenderId();

        stroy = stroyRepository.save(new Stroy(stroyAddDTO.getStrName(),tenderId, stroyAddDTO.getLotId()));

        List<ObjectDTO> objectDTOList = new ArrayList<>();

        for (ObjectAddDTO objectAddDTO : stroyAddDTO.getObArray()) {
            Object saveObj = objectRepository.save(new Object(objectAddDTO.getObName(), objectAddDTO.getObNum(), authLotDTO.getUserId(), stroy));
            List<SmetaDTO> smetaDTOList = new ArrayList<>();
            List<TenderInfoDTO> tenderInfoDTOList;

            for (SmetaAddDTO smetaAddDTO : objectAddDTO.getSmArray()) {
                Smeta saveSmt = smetaRepository.save(new Smeta(smetaAddDTO.getSmName(), smetaAddDTO.getSmNum(), authLotDTO.getUserId(), saveObj, stroy));

                tenderInfoDTOList = saveTender(saveSmt, smetaAddDTO.getSmeta(), authLotDTO);
                SmetaItog smetaItog = smetaItogRepository.save(mapSmetaItogToSmetaItogAddDTO(smetaAddDTO.getSmetaItog(), saveSmt, stroy));
                smetaDTOList.add(mapSmetaToSmetaDTO(saveSmt, tenderInfoDTOList, mapSmetaItogToSmetaItogDTO(smetaItog)));
            }
            objectDTOList.add(mapObjectToObjectDTO(saveObj, smetaDTOList));
        }

        List<SvodResurs> svodResurs = new ArrayList<>();
        for (SvodResursDAO svdResource : stroyAddDTO.getSvodResurs())
            svodResurs.add(mapSvodResource(svdResource, stroy));

        List<SvodResurs> svodResursList = svodResourceRepository.saveAll(svodResurs);

        return ApiResult.successResponse(new StroyDTO(stroy.getId(), stroy.getStrName(), stroy.getTenderId(),
                stroyAddDTO.getLotId(), stroyAddDTO.getInn(), objectDTOList, mapSvodResourceDaoList(svodResursList)));
    }

    private TenderInfoDTO saveTenderCustomerWithChild(Smeta saveSmt, AuthLotDTO authLotDTO, TenderInfoAddDTO tenderInfoAddDTO) {
        TenderCustomer tenderCustomer = tenderCustomerRepository.save(
                mapTenderAddDTOToTender(tenderInfoAddDTO, saveSmt, authLotDTO));

        //ROW_TYPE NI TEKSHIRIB TENDER_CUTOMER LARNI SAQLASH
        if (tenderInfoAddDTO.getRowType() == 0) {
            List<TenderInfoAddDTO> resArray = tenderInfoAddDTO.getResArray();
            if (Objects.isNull(resArray))
                return null;

            List<TenderInfoDTO> resArrList = new ArrayList<>();


            for (TenderInfoAddDTO child : resArray) {
                TenderCustomer tenderCustomerChild = tenderCustomerRepository.save(mapTenderAddDTOToTenderForChild(child, authLotDTO, tenderCustomer));
                resArrList.add(mapTenderToTenderDTO(tenderCustomerChild));
            }

            TenderInfoDTO tenderInfoDTO = mapTenderToTenderDTO(tenderCustomer);
            tenderInfoDTO.setResArray(resArrList);
            return tenderInfoDTO;
        }

        return mapTenderToTenderDTO(tenderCustomer);
    }

    @Override
    @Transactional
    public ApiResult<?> getSmeta(Long innOfferor, Long lotId) {
        AuthLotDTO offerorAuthLotDTO = utils.sendToGetRoleOfLot(innOfferor, lotId);

        //AGAR CUSTOMER BOLIB KELSA HAMMA MALUMOTLARNI QAYTARISH
        if (offerorAuthLotDTO.getRole().equals(RoleEnum.CUSTOMER.name().toLowerCase()))
            return getForCustomer(offerorAuthLotDTO, lotId, innOfferor);

        return getForOfferor(innOfferor, lotId, offerorAuthLotDTO);
    }

    private ApiResult<StroyDTO> getForOfferor(Long innOfferor, Long lotId, AuthLotDTO offerorAuthLotDTO) {
        //SHU OFFERORGA TEGISHLI SHU LOTGA TEGISHLI TENDER MALUMOTLARINI TEKSHIRIB QAYTARISH
        Stroy stroy = stroyRepository.findFirstByLotIdAndDeletedIsFalse(lotId).orElseThrow(
                () -> RestException.restThrow("Not found customer tender's details with  lot_id !", HttpStatus.NOT_FOUND));

        logger.info(String.format("Get all details for offeror: lot_id = %s, userId = %s", lotId, offerorAuthLotDTO.getUserId()));

        List<ObjectDTO> objectDTOList = new ArrayList<>();
        List<Object> obArray = objectRepository.findAllByStroy_Id(stroy.getId());

        for (Object object : obArray) {
            List<SmetaDTO> smetaDTOList = new ArrayList<>();
            List<Smeta> smetaList = smetaRepository.findAllByObject_Id(object.getId());

            for (Smeta smeta : smetaList) {
                List<TenderOfferor> smetaOfferor = tenderOfferorRepository.findAllBySmeta_idAndUserId(smeta.getId(), offerorAuthLotDTO.getUserId());
                List<TenderInfoDTO> list = new ArrayList<>();

                //AGAR OLDIN OFFEROR YUBORILMAGAN BOLSA
                if (smetaOfferor == null || smetaOfferor.isEmpty()) {
                    List<TenderCustomer> smetaCutomers = tenderCustomerRepository.findAllBySmeta_Id(smeta.getId());

                    for (TenderCustomer tenderCustomer : smetaCutomers) {
                        TenderInfoDTO tenderInfoDTO = mapTenderToTenderDTOWithoutPrice(tenderCustomer);

                        if (tenderCustomer.getRowType() == 0)
                            tenderInfoDTO.setResArray(tenderCustomerRepository.findAllByParentId(tenderCustomer.getSmId()).stream()
                                    .map(this::mapTenderToTenderDTOWithoutPrice)
                                    .toList());

                        list.add(tenderInfoDTO);
                    }
                } else {
                    for (TenderOfferor tenderOfferor : smetaOfferor) {
                        TenderInfoDTO tenderInfoDTO = mapTenderToTenderDTO(tenderOfferor);

                        if (tenderOfferor.getRowType() == 0)
                            tenderInfoDTO.setResArray(tenderOfferorRepository.findAllByParentId(tenderOfferor.getSmId()).stream()
                                    .map(this::mapTenderToTenderDTO)
                                    .toList());

                        list.add(tenderInfoDTO);
                    }
                }

                smetaDTOList.add(mapSmetaToSmetaDTO(smeta, list, mapSmetaItogToSmetaItogDTO(smetaItogRepository.findBySmeta_Id(smeta.getId()).orElseThrow())));
            }

            objectDTOList.add(mapObjectToObjectDTO(object, smetaDTOList));
        }

        ///SvodResursni dbdan olib kelish
        List<SvodResursDAO> svodResursDAOSForOfferor = getAllSvodResursForOfferor(stroy, offerorAuthLotDTO.getUserId());

        return ApiResult.successResponse(new StroyDTO(stroy.getId(), stroy.getStrName(), stroy.getTenderId(),
                stroy.getLotId(), innOfferor, objectDTOList, svodResursDAOSForOfferor));
    }

    private List<SvodResursDAO> getAllSvodResursForOfferor(Stroy stroy, long userId) {
        List<SvodResursOfferor> allByStroyForOfferor = svodResourceOfferorRepository.findAllByStroy_Id(stroy.getId());
        List<SvodResursOfferor> svodResursOfferors = new ArrayList<>();

        //agar oldin yaratilmagan bolsa customerdan olib offerorga yozib qaytarish
        if (allByStroyForOfferor == null || allByStroyForOfferor.isEmpty()) {
            List<SvodResurs> svodResursForCustomer = svodResourceRepository.findAllByStroy(stroy.getId());

            for (SvodResurs resurs : svodResursForCustomer)
                svodResursOfferors.add(mapSvodResourceOfferorFromCustomer(resurs, stroy, userId));

            return mapSvodResourceDaoListForOfferor(svodResourceOfferorRepository.saveAll(svodResursOfferors));
        }

        return mapSvodResourceDaoListForOfferorPriceNull(allByStroyForOfferor);
    }

    private ApiResult<?> getForCustomer(AuthLotDTO customerAuthLotDTO, Long lotId, Long innCustomer) {
        Stroy stroy = stroyRepository.findFirstByLotIdAndDeletedIsFalse(lotId).orElseThrow(
                () -> RestException.restThrow("Not found customer tender's details with  lot_id !", HttpStatus.NOT_FOUND));

        logger.info(String.format("Get all details for customer: lot_id = %s, userId = %s", lotId, customerAuthLotDTO.getUserId()));

        List<ObjectDTO> objectDTOList = new ArrayList<>();

        for (Object object : stroy.getObArray()) {
            List<SmetaDTO> smetaDTOList = new ArrayList<>();

            for (Smeta smeta : object.getSmArray()) {
                List<TenderCustomer> smetaCustomers = tenderCustomerRepository.findAllBySmeta_IdAndUserId(smeta.getId(), customerAuthLotDTO.getUserId());
                List<TenderInfoDTO> list = new ArrayList<>();

                for (TenderCustomer smetaCustomer : smetaCustomers) {
                    TenderInfoDTO tenderInfoDTO = mapTenderToTenderDTO(smetaCustomer);

                    if (smetaCustomer.getRowType() == 0)
                        tenderInfoDTO.setResArray(tenderCustomerRepository.findAllByParentId(smetaCustomer.getSmId()).stream()
                                .map(this::mapTenderToTenderDTO)
                                .toList());

                    list.add(tenderInfoDTO);
                }

                smetaDTOList.add(mapSmetaToSmetaDTO(smeta, list, mapSmetaItogToSmetaItogDTO(smeta.getSmetaItog())));
            }

            objectDTOList.add(mapObjectToObjectDTO(object, smetaDTOList));
        }

        ///SvodResursni dbdan olib kelish
        List<SvodResursDAO> svodResursForCustomer = getAllSvodResursForCustomer(stroy);

        return ApiResult.successResponse(new StroyDTO(stroy.getId(), stroy.getStrName(), stroy.getTenderId(),
                stroy.getLotId(), innCustomer, objectDTOList, svodResursForCustomer));
    }

    private List<SvodResursDAO> getAllSvodResursForCustomer(Stroy stroy) {
        return mapSvodResourceDaoList(svodResourceRepository.findAllByStroy(stroy.getId()));
    }

    private List<TenderInfoDTO> saveTender(Smeta smeta, List<TenderInfoAddDTO> smetaDtoList, AuthLotDTO authLotDTO) {
        if (Objects.isNull(smetaDtoList)) return new ArrayList<>();
        List<TenderInfoDTO> list = new ArrayList<>();

        for (TenderInfoAddDTO tenderInfoAddDTO : smetaDtoList)
            list.add(saveTenderCustomerWithChild(smeta, authLotDTO, tenderInfoAddDTO));

        return list;
    }

    private TenderInfoDTO mapTenderToTenderDTO(TenderCustomer tenderCustomer) {
        return TenderInfoDTO.builder()
                .smId(tenderCustomer.getSmId())
                .ed_ism(tenderCustomer.getEdIsm())
                .num(tenderCustomer.getNum())
                .kod_snk(tenderCustomer.getKodSnk())
                .rowType(tenderCustomer.getRowType())
                .price(tenderCustomer.getPrice())
                .name(tenderCustomer.getName())
                .norma(tenderCustomer.getNorma())
                .rashod(tenderCustomer.getRashod())
                .summa(tenderCustomer.getSumma())
                .opred(tenderCustomer.getOpred())
                .userId(tenderCustomer.getUserId())
                .build();
    }

    private TenderInfoDTO mapTenderToTenderDTOWithoutPrice(TenderCustomer tenderCustomer) {
        return TenderInfoDTO.builder()
//                .smId(tenderCustomer.getSmId())
                .ed_ism(tenderCustomer.getEdIsm())
                .num(tenderCustomer.getNum())
                .kod_snk(tenderCustomer.getKodSnk())
                .rowType(tenderCustomer.getRowType())
                .price(null)
                .name(tenderCustomer.getName())
                .norma(tenderCustomer.getNorma())
                .rashod(tenderCustomer.getRashod())
                .summa(null)
                .opred(tenderCustomer.getOpred())
                .userId(tenderCustomer.getUserId())
                .build();
    }

    private TenderInfoDTO mapTenderToTenderDTO(TenderOfferor tender) {
        return TenderInfoDTO.builder()
                .opred(tender.getOpred())
                .smId(tender.getSmId())
                .ed_ism(tender.getEdIsm())
                .num(tender.getNum())
                .kod_snk(tender.getKodSnk())
                .rowType(tender.getRowType())
                .price(tender.getPrice())
                .name(tender.getName())
                .norma(tender.getNorma())
                .rashod(tender.getRashod())
                .summa(tender.getSumma())
                .userId(tender.getUserId())
                .build();
    }

    private TenderCustomer mapTenderAddDTOToTender(TenderInfoAddDTO tenderInfo, Smeta smeta, AuthLotDTO authLotDTO) {
        return TenderCustomer.builder()
                .num(tenderInfo.getNum())
                .edIsm(tenderInfo.getEd_ism())
                .kodSnk(tenderInfo.getKod_snk())
                .rowType(tenderInfo.getRowType())
                .price(tenderInfo.getPrice())
                .name(tenderInfo.getName())
                .norma(tenderInfo.getNorma())
                .rashod(tenderInfo.getRashod())
                .summa(tenderInfo.getSumma())
                .smeta(smeta)
                .opred(tenderInfo.getOpred())
                .lotId(authLotDTO.getLotId())
                .userId(authLotDTO.getUserId())
                .build();
    }

    private TenderCustomer mapTenderAddDTOToTenderForChild(TenderInfoAddDTO tenderInfo, AuthLotDTO authLotDTO, TenderCustomer tenderCustomer) {
        return TenderCustomer.builder()
                .num(tenderInfo.getNum())
                .edIsm(tenderInfo.getEd_ism())
                .kodSnk(tenderInfo.getKod_snk())
                .rowType(tenderInfo.getRowType())
                .price(tenderInfo.getPrice())
                .name(tenderInfo.getName())
                .opred(tenderInfo.getOpred())
                .norma(tenderInfo.getNorma())
                .rashod(tenderInfo.getRashod())
                .summa(tenderInfo.getSumma())
                .smeta(null)
                .lotId(authLotDTO.getLotId())
                .parentId(tenderCustomer.getSmId())
                .userId(authLotDTO.getUserId())
                .build();

    }

    private SmetaDTO mapSmetaToSmetaDTO(Smeta smeta, List<TenderInfoDTO> tenderInfoDTOS, SmetaItogDTO smetaItogDTO) {
        return new SmetaDTO(smeta.getId(), smeta.getSmName(), smeta.getSmNum(), tenderInfoDTOS, smetaItogDTO);
    }

    private ObjectDTO mapObjectToObjectDTO(Object object, List<SmetaDTO> smetaDTOS) {
        return new ObjectDTO(object.getId(), object.getObName(), object.getObNum(), smetaDTOS);
    }

    private SmetaItogDTO mapSmetaItogToSmetaItogDTO(SmetaItog smetaItog) {
        return SmetaItogDTO.builder()
                .itogPr(smetaItog.getItogPr())
                .itogAll(smetaItog.getItogAll())
                .zatrTrud(smetaItog.getZatrTrud())
                .summaZp(smetaItog.getSummaZp())
                .summaExp(smetaItog.getSummaExp())
                .summaMat(smetaItog.getSummaMat())
                .summaObo(smetaItog.getSummaObo())
                .summaPph(smetaItog.getSummaPph())
                .summaPzp(smetaItog.getSummaPzp())
                .summaSso(smetaItog.getSummaSso())
                .summaKr(smetaItog.getSummaKr())
                .summaNds(smetaItog.getSummaNds())
                .build();
    }

    private SmetaItog mapSmetaItogToSmetaItogAddDTO(SmetaItogAddDTO smetaItog, Smeta saveSmt, Stroy stroy) {
        return SmetaItog.builder()
                .itogPr(smetaItog.getItogPr())
                .itogAll(smetaItog.getItogAll())
                .zatrTrud(smetaItog.getZatrTrud())
                .summaZp(smetaItog.getSummaZp())
                .summaExp(smetaItog.getSummaExp())
                .summaMat(smetaItog.getSummaMat())
                .summaObo(smetaItog.getSummaObo())
                .summaPph(smetaItog.getSummaPph())
                .summaPzp(smetaItog.getSummaPzp())
                .summaSso(smetaItog.getSummaSso())
                .summaKr(smetaItog.getSummaKr())
                .summaNds(smetaItog.getSummaNds())
                .smeta(saveSmt)
                .stroy(stroy)
                .build();
    }

    private SvodResurs mapSvodResource(SvodResursDAO svdResource, Stroy stroy) {
        return new SvodResurs(
                svdResource.getNum(),
                svdResource.getKodv(),
                svdResource.getTip(),
                svdResource.getKodr(),
                svdResource.getKodm(),
                svdResource.getKodiName(),
                svdResource.getName(),
                svdResource.getKol(),
                svdResource.getPrice(),
                svdResource.getSumma(),
                stroy, false
        );
    }

    private List<SvodResursDAO> mapSvodResourceDaoList(List<SvodResurs> svodResursList) {
        return svodResursList.stream().map(svodResurs ->
                new SvodResursDAO(
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
                )).toList();
    }

    private List<SvodResursDAO> mapSvodResourceDaoListForOfferor(List<SvodResursOfferor> svodResursList) {
        return svodResursList.stream().map(svodResurs ->
                new SvodResursDAO(
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
                )).toList();
    }

    private List<SvodResursDAO> mapSvodResourceDaoListForOfferorPriceNull(List<SvodResursOfferor> svodResursList) {
        return svodResursList.stream().map(svodResurs ->
                new SvodResursDAO(
                        svodResurs.getId(),
                        svodResurs.getNum(),
                        svodResurs.getKodv(),
                        svodResurs.getTip(),
                        svodResurs.getKodr(),
                        svodResurs.getKodm(),
                        svodResurs.getKodiName(),
                        svodResurs.getName(),
                        svodResurs.getKol(),
                        null,
                        null
                )).toList();
    }

    private SvodResursOfferor mapSvodResourceOfferorFromCustomer(SvodResurs svodResursForCustomer, Stroy stroy, long userId) {
        return new SvodResursOfferor(
                svodResursForCustomer.getNum(),
                svodResursForCustomer.getKodv(),
                svodResursForCustomer.getTip(),
                svodResursForCustomer.getKodr(),
                svodResursForCustomer.getKodm(),
                svodResursForCustomer.getKodiName(),
                svodResursForCustomer.getName(),
                svodResursForCustomer.getKol(),
                svodResursForCustomer.getPrice(),
                svodResursForCustomer.getSumma(),
                stroy, userId, false
        );
    }

}
