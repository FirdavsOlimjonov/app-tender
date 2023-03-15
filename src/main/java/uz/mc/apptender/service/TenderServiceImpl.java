package uz.mc.apptender.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import uz.mc.apptender.exeptions.RestException;
import uz.mc.apptender.modules.*;
import uz.mc.apptender.modules.Object;
import uz.mc.apptender.modules.enums.RoleEnum;
import uz.mc.apptender.payload.*;
import uz.mc.apptender.repositories.*;

import java.net.URI;
import java.util.*;

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

        AuthLotDTO authLotDTO = sendToGetRoleOfLot(stroyAddDTO);

        logger.info(String.format("Request send for get role and auth: role = %s, userId = %s", authLotDTO.getRole(), authLotDTO.getUserId()));

        List<TenderInfoDTO> tenderInfoDTOList;
        List<SmetaDTO> smetaDTOList = new ArrayList<>();
        List<ObjectDTO> objectDTOList = new ArrayList<>();

        RoleEnum role = authLotDTO.getRole().toUpperCase().equals(RoleEnum.CUSTOMER.name()) ? RoleEnum.CUSTOMER : RoleEnum.OFFEROR;

        //AGAR STROY TOPILSA SHUNGA TEGISHLI HAMMA DETAILSLARNI DELETED TRUE GA OTKIZIB CHIQISH UCHUN
        Stroy stroy = stroyRepository.findFirstByLotIdAndRoleAndUserIdAndDeletedIsFalse(stroyAddDTO.getLotId(), role, authLotDTO.getUserId()).orElse(new Stroy());

        if (Objects.nonNull(stroy.getId())) {
            stroyRepository.deleteAllByUserAndRole(role.name(), authLotDTO.getUserId(), stroy.getId());
            stroy.setDeleted(true);
            stroyRepository.save(stroy);
        }

        Stroy saveStroy = stroyRepository.save(new Stroy(stroyAddDTO.getStrName(),tenderId, authLotDTO.getUserId(), stroyAddDTO.getLotId(), role));

        for (ObjectAddDTO objectAddDTO : stroyAddDTO.getObArray()) {
            Object saveObj = objectRepository.save(new Object(objectAddDTO.getObName(), objectAddDTO.getObNum(), role, authLotDTO.getUserId(), saveStroy));

            for (SmetaAddDTO smetaAddDTO : objectAddDTO.getSmArray()) {
                Smeta saveSmt = smetaRepository.save(new Smeta(smetaAddDTO.getSmName(), smetaAddDTO.getSmNum(), role, authLotDTO.getUserId(), saveObj));

                tenderInfoDTOList = saveTender(saveSmt, smetaAddDTO.getSmeta(), authLotDTO, role);
                smetaDTOList.add(mapSmetaToSmetaDTO(saveSmt, tenderInfoDTOList));
            }
            objectDTOList.add(mapObjectToObjectDTO(saveObj, smetaDTOList));
        }

        return ApiResult.successResponse(new StroyDTO(saveStroy.getId(), saveStroy.getStrName(), saveStroy.getTenderId(),
                stroyAddDTO.getLotId(), stroyAddDTO.getInn(), authLotDTO.getRole(), objectDTOList));
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

    private List<TenderInfoDTO> saveTender(Smeta smeta, List<TenderInfoAddDTO> smetaDtoList, AuthLotDTO authLotDTO, RoleEnum role) {
        if (Objects.isNull(smetaDtoList))
            throw RestException.restThrow("Smeta array must not be empty", HttpStatus.BAD_REQUEST);

        if (authLotDTO.getRole().toUpperCase().equals(RoleEnum.CUSTOMER.name()))
            return smetaDtoList.stream().map(smetaDto -> {
                TenderCustomer tenderCustomer = tenderCustomerRepository.save(mapTenderAddDTOToTender(smetaDto, smeta, authLotDTO, role));
                return mapTenderToTenderDTO(tenderCustomer);
            }).toList();

        return smetaDtoList.stream().map(smetaDto -> {
            TenderOfferor tenderOfferor = tenderOfferorRepository.save(mapTenderAddDTOToTenderOfferor(smetaDto, smeta, authLotDTO, role));
            return mapTenderToTenderDTO(tenderOfferor);
        }).toList();
    }

    private AuthLotDTO sendToGetRoleOfLot(StroyAddDTO stroyAddDTO) {
        URI uri = UriComponentsBuilder.fromUriString(apiUrlToRole).build().toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBasicAuth(username, password);

        //LOT_ID AND INN SEND TO MUHAMMADALI'S SERVER WITH BASIC AUTH
        logger.info("Send to get role and user_id from Muhammadali's server with inn: " + stroyAddDTO.getInn() + " lod_id: " + stroyAddDTO.getLotId());
        HttpEntity<CreateTenderDTO> requestEntity = new HttpEntity<>(new CreateTenderDTO(stroyAddDTO.getLotId(), stroyAddDTO.getInn()), headers);

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
                .role(tenderCustomer.getRole())
                .build();
    }

    private TenderInfoDTO mapTenderToTenderDTO(TenderOfferor tender) {
        return TenderInfoDTO.builder()
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
                .role(tender.getRole())
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
                .role(role)
                .build();
    }

    private TenderOfferor mapTenderAddDTOToTenderOfferor(TenderInfoAddDTO tenderInfo, Smeta smeta, AuthLotDTO authLotDTO, RoleEnum role) {
        return TenderOfferor.builder()
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
                .role(role)
                .build();
    }

    private SmetaDTO mapSmetaToSmetaDTO(Smeta smeta, List<TenderInfoDTO> tenderInfoDTOS) {
        return new SmetaDTO(smeta.getId(), smeta.getSmName(), smeta.getSmNum(), tenderInfoDTOS);
    }

    private ObjectDTO mapObjectToObjectDTO(Object object, List<SmetaDTO> smetaDTOS) {
        return new ObjectDTO(object.getId(), object.getObName(), object.getObNum(), smetaDTOS);
    }
}
