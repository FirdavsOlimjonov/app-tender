package uz.mc.apptender.service;

import com.fasterxml.jackson.databind.JsonNode;
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
import uz.mc.apptender.payload.*;
import uz.mc.apptender.repositories.*;

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
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${app.rest-template.username}")
    private String username;
    @Value("${app.rest-template.password}")
    private String password;
    @Value("${app.rest-template.url-send-sms}")
    private String apiUrl;
    @Value("${app.rest-template.url-get-role}")
    private String apiUrlToRole;
    private static final Logger logger = LoggerFactory.getLogger(TenderServiceImpl.class);

    @Override
    @Transactional
    public ApiResult<StroyDTO> add(StroyAddDTO stroyAddDTO) {
        Integer maxTenderId = stroyRepository.findMaxTenderId();
        Integer tenderId = Objects.isNull(maxTenderId) ? 1 : maxTenderId + 1;

        AuthLotDTO authLotDTO = sendToGetRoleOfLot(stroyAddDTO.getInn(), stroyAddDTO.getLotId());
        RoleEnum role = authLotDTO.getRole().toUpperCase().equals(RoleEnum.CUSTOMER.name()) ? RoleEnum.CUSTOMER : RoleEnum.OFFEROR;

        logger.info(String.format("Request send for get role and auth: role = %s, userId = %s", authLotDTO.getRole(), authLotDTO.getUserId()));

        //AGAR OFFEROR BOLSA UNI FAQAT PRICENI SAQLAYMIZ
        if (role.equals(RoleEnum.OFFEROR))
            return ApiResult.successResponse(saveTenderForOfferor(stroyAddDTO, authLotDTO));

        //AGAR STROY TOPILSA SHUNGA TEGISHLI HAMMA DETAILSLARNI DELETED TRUE GA OTKIZIB CHIQISH UCHUN
        Stroy stroy = stroyRepository.findFirstByLotIdAndDeletedIsFalse(stroyAddDTO.getLotId()).orElse(new Stroy());

        if (Objects.nonNull((stroy.getId()))) {
            if (Objects.isNull(stroyAddDTO.getId()))
                throw RestException.restThrow("Smeta already created with this lot_id! You should give unique id for update this smeta details!");
            return updateTenderFromCustomer(authLotDTO, stroy, stroyAddDTO, role);
        }

        Stroy saveStroy;
        if (!authLotDTO.getRole().equals(RoleEnum.OFFEROR.name().toLowerCase()))
            saveStroy = stroyRepository.save(new Stroy(stroyAddDTO.getStrName(), tenderId, stroyAddDTO.getLotId()));
        else saveStroy = stroy;

        List<ObjectDTO> objectDTOList = new ArrayList<>();

        for (ObjectAddDTO objectAddDTO : stroyAddDTO.getObArray()) {
            Object saveObj = objectRepository.save(new Object(objectAddDTO.getObName(), objectAddDTO.getObNum(), role, authLotDTO.getUserId(), saveStroy));
            List<SmetaDTO> smetaDTOList = new ArrayList<>();
            List<TenderInfoDTO> tenderInfoDTOList;

            for (SmetaAddDTO smetaAddDTO : objectAddDTO.getSmArray()) {
                Smeta saveSmt = smetaRepository.save(new Smeta(smetaAddDTO.getSmName(), smetaAddDTO.getSmNum(), role, authLotDTO.getUserId(), saveObj));

                tenderInfoDTOList = saveTender(saveSmt, smetaAddDTO.getSmeta(), authLotDTO, role);
                smetaDTOList.add(mapSmetaToSmetaDTO(saveSmt, tenderInfoDTOList));
            }
            objectDTOList.add(mapObjectToObjectDTO(saveObj, smetaDTOList));
        }

        return ApiResult.successResponse(new StroyDTO(saveStroy.getId(), saveStroy.getStrName(), saveStroy.getTenderId(),
                stroyAddDTO.getLotId(), stroyAddDTO.getInn(), objectDTOList));
    }

    private ApiResult<StroyDTO> updateTenderFromCustomer(AuthLotDTO authLotDTO, Stroy stroy, StroyAddDTO stroyAddDTO, RoleEnum role) {

        stroy.setStrName(stroyAddDTO.getStrName());
        Stroy save = stroyRepository.save(stroy);

        List<ObjectDTO> objectDTOList = new ArrayList<>();

        for (ObjectAddDTO objectAddDTO : stroyAddDTO.getObArray()) {
            Object saveObj;
            List<SmetaDTO> smetaDTOList = new ArrayList<>();

            if (Objects.isNull(objectAddDTO.getId()))
                saveObj = objectRepository.save(new Object(objectAddDTO.getObName(), objectAddDTO.getObNum(), role, authLotDTO.getUserId(), save));
            else {
                Object object = objectRepository.findById(objectAddDTO.getId()).orElseThrow(
                        () -> RestException.restThrow("Object not found !" + objectAddDTO.getId()));

                object.setObName(objectAddDTO.getObName());
                object.setRole(role);
                saveObj = objectRepository.save(object);
            }

            for (SmetaAddDTO smetaAddDTO : objectAddDTO.getSmArray()) {
                Smeta saveSmt;
                List<TenderInfoDTO> tenderInfoDTOS = new ArrayList<>();

                if (Objects.isNull(smetaAddDTO.getId()))
                    saveSmt = smetaRepository.save(new Smeta(smetaAddDTO.getSmName(), smetaAddDTO.getSmNum(), role, authLotDTO.getUserId(), saveObj));
                else {
                    Smeta smeta = smetaRepository.findById(smetaAddDTO.getId()).orElseThrow(
                            () -> RestException.restThrow("Smeta not found !" + smetaAddDTO.getId()));

                    smeta.setSmName(smetaAddDTO.getSmName());
                    smeta.setSmNum(smetaAddDTO.getSmNum());
                    smeta.setRole(role);

                    saveSmt = smetaRepository.save(smeta);
                }

                for (TenderInfoAddDTO tenderInfoAddDTO : smetaAddDTO.getSmeta()) {
                    TenderCustomer tenderCustomer;

                    if (Objects.isNull(tenderInfoAddDTO.getSmId()))
                        tenderCustomer = tenderCustomerRepository.save(mapTenderAddDTOToTender(tenderInfoAddDTO, saveSmt, authLotDTO, role));
                    else {

                        TenderCustomer customer = tenderCustomerRepository.findBySmId(tenderInfoAddDTO.getSmId()).orElseThrow(
                                () -> RestException.restThrow("Smeta  not found !" + tenderInfoAddDTO.getSmId()));

                        customer.setEdIsm(tenderInfoAddDTO.getEd_ism());
                        customer.setId(tenderInfoAddDTO.getId());
                        customer.setType(tenderInfoAddDTO.getType());
                        customer.setRashod(tenderInfoAddDTO.getRashod());
                        customer.setNorma(tenderInfoAddDTO.getNorma());
                        customer.setSumma(tenderInfoAddDTO.getSumma());
                        customer.setKodSnk(tenderInfoAddDTO.getKod_snk());
                        customer.setPrice(tenderInfoAddDTO.getPrice());
                        customer.setName(tenderInfoAddDTO.getName());

                        tenderCustomer = tenderCustomerRepository.save(customer);

                    }
                    tenderInfoDTOS.add(mapTenderToTenderDTO(tenderCustomer));

                }
                smetaDTOList.add(mapSmetaToSmetaDTO(saveSmt, tenderInfoDTOS));

            }

            objectDTOList.add(mapObjectToObjectDTO(saveObj, smetaDTOList));
        }

        return ApiResult.successResponse(new StroyDTO(save.getId(), save.getStrName(), save.getTenderId(),
                stroyAddDTO.getLotId(), stroyAddDTO.getInn(), objectDTOList));
    }

    private StroyDTO saveTenderForOfferor(StroyAddDTO stroyAddDTO, AuthLotDTO authLotDTO) {
        Long lotId = stroyAddDTO.getLotId();

        Stroy stroy = stroyRepository.findFirstByLotId(lotId).orElseThrow(
                () -> RestException.restThrow("This lot not found!", HttpStatus.NOT_FOUND));

        List<ObjectDTO> objectDTOList = new ArrayList<>();


        for (ObjectAddDTO objectAddDTO : stroyAddDTO.getObArray()) {
            if (Objects.isNull(objectAddDTO.getId()))
                throw RestException.restThrow("You should give Object Id!");

            Object object = objectRepository.findById(objectAddDTO.getId()).orElseThrow(
                    () -> RestException.restThrow("Object not found!", HttpStatus.NOT_FOUND));

            List<SmetaDTO> smetaDTOList = new ArrayList<>();

            for (SmetaAddDTO smetaAddDTO : objectAddDTO.getSmArray()) {
                if (Objects.isNull(smetaAddDTO.getId()))
                    throw RestException.restThrow("You should give Smeta Id!");

                Smeta smeta = smetaRepository.findById(smetaAddDTO.getId()).orElseThrow(
                        () -> RestException.restThrow("Smeta not found!", HttpStatus.NOT_FOUND));

                List<TenderInfoDTO> tenderInfoDTOS = new ArrayList<>();

                for (TenderInfoAddDTO tenderInfoAddDTO : smetaAddDTO.getSmeta()) {
                    if (Objects.isNull(tenderInfoAddDTO.getId()))
                        throw RestException.restThrow("You should give SmId!");

                    TenderOfferor tenderOfferor = tenderOfferorRepository.findBySmIdAndUserId(tenderInfoAddDTO.getSmId(), authLotDTO.getUserId())
                            .orElseThrow(() -> RestException.restThrow("Tender Offeror not found!", HttpStatus.NOT_FOUND));

                    tenderOfferor.setPrice(tenderInfoAddDTO.getPrice());
                    tenderOfferor.setSumma(tenderInfoAddDTO.getSumma());

                    TenderOfferor save = tenderOfferorRepository.save(tenderOfferor);
                    tenderInfoDTOS.add(mapTenderToTenderDTO(save));

                }
                smetaDTOList.add(mapSmetaToSmetaDTO(smeta, tenderInfoDTOS));

            }

            objectDTOList.add(mapObjectToObjectDTO(object, smetaDTOList));

        }

        return new StroyDTO(stroy.getId(), stroy.getStrName(), stroy.getTenderId(),
                stroy.getLotId(), stroyAddDTO.getInn(), objectDTOList);


    }

    @Override
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
            throw RestException.restThrow(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        // EXTRACT ROLE AND CODE FROM THE RESPONSE DTO OBJECT
        String role = Objects.requireNonNull(tempTenderDTO).getResult().getData().getRole();
        String code = tempTenderDTO.getResult().getData().getCode();

        return ApiResult.successResponse(new TenderAuthDTO(createTenderDTO.getLotId(), createTenderDTO.getInn(), role, code));
    }

    @Override
    public ApiResult<?> getForOfferor(Long innOfferor, Long lotId) {
        AuthLotDTO offerorAuthLotDTO = sendToGetRoleOfLot(innOfferor, lotId);

        //AGAR OFFERORDAN BOSHQA ODAM KELSA QAYTARAVORADI
        if (!offerorAuthLotDTO.getRole().equals(RoleEnum.OFFEROR.name().toLowerCase()))
            return getForcustomer(offerorAuthLotDTO, lotId, innOfferor);


        //SHU OFFERORGA TEGISHLI SHU LOTGA TEGISHLI TENDER MALUMOTLARINI TEKSHIRIB QAYTARISH
        Stroy stroy = stroyRepository.findFirstByLotIdAndDeletedIsFalse(lotId).orElseThrow(
                () -> RestException.restThrow("Not found customer tender's details with  lot_id !", HttpStatus.NOT_FOUND));

        List<ObjectDTO> objectDTOList = new ArrayList<>();

        for (Object object : stroy.getObArray()) {
            List<SmetaDTO> smetaDTOList = new ArrayList<>();

            for (Smeta smeta : object.getSmArray()) {
                List<TenderOfferor> smetaOfferor = smeta.getSmeta_offeror();
                List<TenderInfoDTO> list = new ArrayList<>();

                //AGAR OLDIN OFFEROR YUBORILMAGAN BOLSA
                if (smetaOfferor.isEmpty()) {
                    for (TenderCustomer tenderCustomer : smeta.getSmeta_customer()) {
                        TenderOfferor save = tenderOfferorRepository.save(mapTenderOfferorToTenderCustomer(tenderCustomer, smeta, offerorAuthLotDTO));
                        list.add(mapTenderToTenderDTO(save));
                    }
                } else {
                    list = smetaOfferor.stream()
                            .filter(offeror -> offeror.getUserId() == offerorAuthLotDTO.getUserId())
                            .map(this::mapTenderToTenderDTO)
                            .toList();
                }

                List<TenderInfoDTO> list2 = new ArrayList<>();
                if (list.isEmpty())
                    for (TenderCustomer tenderCustomer : smeta.getSmeta_customer()) {
                        TenderOfferor save = tenderOfferorRepository.save(mapTenderOfferorToTenderCustomer(tenderCustomer, smeta, offerorAuthLotDTO));
                        list2.add(mapTenderToTenderDTO(save));
                    }

                list = list2.isEmpty() ? list : list2;
                smetaDTOList.add(mapSmetaToSmetaDTO(smeta, list));
            }

            objectDTOList.add(mapObjectToObjectDTO(object, smetaDTOList));
        }

        return ApiResult.successResponse(new StroyDTO(stroy.getId(), stroy.getStrName(), stroy.getTenderId(),
                stroy.getLotId(), innOfferor, objectDTOList));
    }

    private ApiResult<?> getForcustomer(AuthLotDTO customerAuthLotDTO, Long lotId, Long innOfferor) {
        Stroy stroy = stroyRepository.findFirstByLotIdAndDeletedIsFalse(lotId).orElseThrow(
                () -> RestException.restThrow("Not found customer tender's details with  lot_id !", HttpStatus.NOT_FOUND));

        List<ObjectDTO> objectDTOList = new ArrayList<>();

        for (Object object : stroy.getObArray()) {
            List<SmetaDTO> smetaDTOList = new ArrayList<>();

            for (Smeta smeta : object.getSmArray()) {
                List<TenderCustomer> smetaCustomer = smeta.getSmeta_customer();
                List<TenderInfoDTO> list = new ArrayList<>();

                if (!smetaCustomer.isEmpty())
                    list = smetaCustomer.stream()
                            .filter(customer -> customer.getUserId() == customerAuthLotDTO.getUserId())
                            .map(this::mapTenderToTenderDTO)
                            .toList();

                smetaDTOList.add(mapSmetaToSmetaDTO(smeta, list));
            }

            objectDTOList.add(mapObjectToObjectDTO(object, smetaDTOList));
        }

        return ApiResult.successResponse(new StroyDTO(stroy.getId(), stroy.getStrName(), stroy.getTenderId(),
                stroy.getLotId(), innOfferor, objectDTOList));
    }

    private TenderOfferor mapTenderOfferorToTenderCustomer(TenderCustomer tenderCustomer, Smeta smeta, AuthLotDTO offerorAuthLotDTO) {
        return TenderOfferor.builder()
                .norma(tenderCustomer.getNorma())
                .price(null)
                .name(tenderCustomer.getName())
                .kodSnk(tenderCustomer.getKodSnk())
                .rashod(tenderCustomer.getRashod())
                .summa(null)
                .type(tenderCustomer.getType())
                .edIsm(tenderCustomer.getEdIsm())
                .id(tenderCustomer.getId())
                .smeta(smeta)
                .userId(offerorAuthLotDTO.getUserId())
                .build();
    }

    private List<TenderInfoDTO> saveTender(Smeta smeta, List<TenderInfoAddDTO> smetaDtoList, AuthLotDTO authLotDTO, RoleEnum role) {
        if (Objects.isNull(smetaDtoList))
            throw RestException.restThrow("Smeta array must not be empty", HttpStatus.BAD_REQUEST);

        return smetaDtoList.stream().map(smetaDto -> {
            TenderCustomer tenderCustomer = tenderCustomerRepository.save(mapTenderAddDTOToTender(smetaDto, smeta, authLotDTO, role));
            return mapTenderToTenderDTO(tenderCustomer);
        }).toList();

    }

    private AuthLotDTO sendToGetRoleOfLot(Long inn, Long lotId) {
        URI uri = UriComponentsBuilder.fromUriString(apiUrlToRole).build().toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBasicAuth(username, password);

        //LOT_ID AND INN SEND TO MUHAMMADALI'S SERVER WITH BASIC AUTH
        logger.info("Send to get role and user_id from Muhammadali's server with inn: " + inn + " lod_id: " + lotId);
        HttpEntity<CreateTenderDTO> requestEntity = new HttpEntity<>(new CreateTenderDTO(lotId, inn), headers);

        JsonNode jsonNode;
        try {
            jsonNode = restTemplate.postForObject(uri, requestEntity, JsonNode.class);
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            e.fillInStackTrace();
            throw RestException.restThrow("Invalid inn or lot id", HttpStatus.BAD_REQUEST);
        }

        AuthLotDTO authLotDTO = new AuthLotDTO();

        if (Objects.nonNull(jsonNode)) {
            String role = jsonNode.get("result").get("data").get("role").asText();
            long userId = jsonNode.get("result").get("data").get("user_id").asLong();

            authLotDTO.setRole(role);
            authLotDTO.setUserId(userId);
        }

        return authLotDTO;
    }

    private TenderInfoDTO mapTenderToTenderDTO(TenderCustomer tenderCustomer) {
        return TenderInfoDTO.builder()
                .smId(tenderCustomer.getSmId())
                .ed_ism(tenderCustomer.getEdIsm())
                .id(tenderCustomer.getId())
                .kod_snk(tenderCustomer.getKodSnk())
                .type(tenderCustomer.getType())
                .price(tenderCustomer.getPrice())
                .name(tenderCustomer.getName())
                .norma(tenderCustomer.getNorma())
                .rashod(tenderCustomer.getRashod())
                .summa(tenderCustomer.getSumma())
                .userId(tenderCustomer.getUserId())
                .build();
    }

    private TenderInfoDTO mapTenderToTenderDTO(TenderOfferor tender) {
        return TenderInfoDTO.builder()
                .smId(tender.getSmId())
                .ed_ism(tender.getEdIsm())
                .id(tender.getId())
                .kod_snk(tender.getKodSnk())
                .type(tender.getType())
                .price(tender.getPrice())
                .name(tender.getName())
                .norma(tender.getNorma())
                .rashod(tender.getRashod())
                .summa(tender.getSumma())
                .userId(tender.getUserId())
                .build();
    }

    private TenderCustomer mapTenderAddDTOToTender(TenderInfoAddDTO tenderInfo, Smeta smeta, AuthLotDTO authLotDTO, RoleEnum role) {
        return TenderCustomer.builder()
                .id(tenderInfo.getId())
                .edIsm(tenderInfo.getEd_ism())
                .kodSnk(tenderInfo.getKod_snk())
                .type(tenderInfo.getType())
                .price(tenderInfo.getPrice())
                .name(tenderInfo.getName())
                .norma(tenderInfo.getNorma())
                .rashod(tenderInfo.getRashod())
                .summa(tenderInfo.getSumma())
                .smeta(smeta)
                .userId(authLotDTO.getUserId())
                .build();
    }

    private SmetaDTO mapSmetaToSmetaDTO(Smeta smeta, List<TenderInfoDTO> tenderInfoDTOS) {
        return new SmetaDTO(smeta.getId(), smeta.getSmName(), smeta.getSmNum(), tenderInfoDTOS);
    }

    private ObjectDTO mapObjectToObjectDTO(Object object, List<SmetaDTO> smetaDTOS) {
        return new ObjectDTO(object.getId(), object.getObName(), object.getObNum(), smetaDTOS);
    }

}
