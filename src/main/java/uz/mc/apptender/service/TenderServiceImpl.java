package uz.mc.apptender.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
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
    private final TenderRepository tenderRepository;
    private final TenderOfferorRepository tenderOfferorRepository;
    private final StroyRepository stroyRepository;
    private final ObjectRepository objectRepository;
    private final SmetaRepository smetaRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    private static final String username = "admin@mail.com";
    private static final String password = "password";
    private static final String apiUrl = "https://apistender-test1.mc.uz/api/send-sms-to-lot-user";
    private static final String apiUrlToRole = "https://apistender-test1.mc.uz/api/get-role-of-lot-user";
    private static final Logger logger = LoggerFactory.getLogger(TenderServiceImpl.class);
    private Integer tenderId;

    @Override
    public ApiResult<StroyDTO> add(StroyAddDTO stroyAddDTO) {
        Integer maxTenderId = stroyRepository.findMaxTenderId();
        tenderId = Objects.isNull(maxTenderId) ? 1 : maxTenderId + 1;

        AuthLotDTO authLotDTO = sendToGetRoleOfLot(stroyAddDTO);

        List<TenderInfoDTO> tenderInfoDTOList;
        List<SmetaDTO> smetaDTOList = new ArrayList<>();
        List<ObjectDTO> objectDTOList = new ArrayList<>();

        Stroy stroy = stroyRepository.save(new Stroy(stroyAddDTO.getStrName(), tenderId, authLotDTO.getUserId()));

        for (ObjectAddDTO objectAddDTO : stroyAddDTO.getObArray()) {
            Object object = objectRepository.save(new Object(objectAddDTO.getObName(), objectAddDTO.getObNum(), stroy));

            for (SmetaAddDTO smetaAddDTO : objectAddDTO.getSmArray()) {
                Smeta smeta = smetaRepository.save(new Smeta(smetaAddDTO.getSmName(), smetaAddDTO.getSmNum(), object));
                tenderInfoDTOList = saveTender(smeta, smetaAddDTO.getSmeta(), authLotDTO);
                smetaDTOList.add(mapSmetaToSmetaDTO(smeta, tenderInfoDTOList));
            }
            objectDTOList.add(mapObjectToObjectDTO(object, smetaDTOList));
        }

        // todo roleni Muhammad akadan olish
        return ApiResult.successResponse(new StroyDTO(stroy.getId(), stroy.getStrName(), stroy.getTenderId(),
                stroyAddDTO.getLotId(), stroyAddDTO.getInn(), "Role", objectDTOList));
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
        TempTenderDTO tempTenderDTO = restTemplate.postForObject(uri, requestEntity, TempTenderDTO.class);

        // EXTRACT ROLE AND CODE FROM THE RESPONSE DTO OBJECT
        String role = Objects.requireNonNull(tempTenderDTO).getResult().getData().getRole();
        String code = tempTenderDTO.getResult().getData().getCode();

        return ApiResult.successResponse(new TenderAuthDTO(createTenderDTO.getLotId(), createTenderDTO.getInn(), role, code));
    }

    private List<TenderInfoDTO> saveTender(Smeta smeta, List<TenderInfoAddDTO> smetaDtoList, AuthLotDTO authLotDTO) {
        if (Objects.isNull(smetaDtoList))
            throw RestException.restThrow("Smeta array must not be empty", HttpStatus.BAD_REQUEST);

        if (authLotDTO.getRole().equals(RoleEnum.CUSTOMER))
            return smetaDtoList.stream().map(smetaDto -> {
                Tender tender = tenderRepository.save(mapTenderAddDTOToTender(smetaDto, smeta, authLotDTO));
                return mapTenderToTenderDTO(tender);
            }).toList();

        return smetaDtoList.stream().map(smetaDto -> {
            TenderOfferor tenderOfferor = tenderOfferorRepository.save(mapTenderAddDTOToTenderOfferor(smetaDto, smeta, authLotDTO));
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

        return restTemplate.postForObject(uri, requestEntity, AuthLotDTO.class);
    }

    private TenderInfoDTO mapTenderToTenderDTO(Tender tender) {
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

    private Tender mapTenderAddDTOToTender(TenderInfoAddDTO tenderInfo, Smeta smeta, AuthLotDTO authLotDTO) {
        return Tender.builder()
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
                .role(authLotDTO.getRole())
                .build();
    }

    private TenderOfferor mapTenderAddDTOToTenderOfferor(TenderInfoAddDTO tenderInfo, Smeta smeta, AuthLotDTO authLotDTO) {
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
                .role(authLotDTO.getRole())
                .build();
    }

    private SmetaDTO mapSmetaToSmetaDTO(Smeta smeta, List<TenderInfoDTO> tenderInfoDTOS) {
        return new SmetaDTO(smeta.getId(), smeta.getSmName(), smeta.getSmNum(), tenderInfoDTOS);
    }

    private ObjectDTO mapObjectToObjectDTO(Object object, List<SmetaDTO> smetaDTOS) {
        return new ObjectDTO(object.getId(), object.getObName(), object.getObNum(), smetaDTOS);
    }
}
