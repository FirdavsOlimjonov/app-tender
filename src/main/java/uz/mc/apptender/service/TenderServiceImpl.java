package uz.mc.apptender.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import uz.mc.apptender.exeptions.RestException;
import uz.mc.apptender.modules.*;
import uz.mc.apptender.modules.Object;
import uz.mc.apptender.payload.*;
import uz.mc.apptender.repositories.ObjectRepository;
import uz.mc.apptender.repositories.SmetaRepository;
import uz.mc.apptender.repositories.StroyRepository;
import uz.mc.apptender.repositories.LotRepository;

import java.net.URI;
import java.util.*;

@Service
@RequiredArgsConstructor
public class TenderServiceImpl implements TenderService {
    private final LotRepository lotRepository;
    private final StroyRepository stroyRepository;
    private final ObjectRepository objectRepository;
    private final SmetaRepository smetaRepository;
    private static final String username = "admin@mail.com";
    private static final String password = "password";
    private static final String apiUrl = "https://apistender-test1.mc.uz/api/send-sms-to-lot-user";
    private static final Logger logger = LoggerFactory.getLogger(TenderServiceImpl.class);
    private Integer tenderId;

    @Override
    public ApiResult<StroyDTO> add(StroyAddDTO stroyAddDTO) {
        Integer maxTenderId = stroyRepository.findMaxTenderId();
        tenderId = Objects.isNull(maxTenderId)?1:maxTenderId+1;

        List<TenderInfoDTO> tenderInfoDTOList;
        List<SmetaDTO> smetaDTOList = new ArrayList<>();
        List<ObjectDTO> objectDTOList = new ArrayList<>();

        Stroy stroy = stroyRepository.save(new Stroy(stroyAddDTO.getStrName(), tenderId));
        for (ObjectAddDTO objectAddDTO : stroyAddDTO.getObArray()) {
            Object object = objectRepository.save(new Object(objectAddDTO.getObName(), objectAddDTO.getObNum(), stroy));

            for (SmetaAddDTO smetaAddDTO : objectAddDTO.getSmArray()) {
                Smeta smeta = smetaRepository.save(new Smeta(smetaAddDTO.getSmName(), smetaAddDTO.getSmNum(), object));
                tenderInfoDTOList = saveTender(smeta, smetaAddDTO.getSmeta());
                smetaDTOList.add(mapSmetaToSmetaDTO(smeta, tenderInfoDTOList));
            }
            objectDTOList.add(mapObjectToObjectDTO(object, smetaDTOList));
        }

        return ApiResult.successResponse(new StroyDTO(stroy.getId(), stroy.getStrName(), stroy.getTenderId(),
                stroyAddDTO.getLotId(), stroyAddDTO.getInn(), stroyAddDTO.getRole(), objectDTOList));
    }

    @Override
    public ApiResult<?> createTender(CreateTenderDTO createTenderDTO) {
        RestTemplate restTemplate = new RestTemplate();
        URI uri = UriComponentsBuilder.fromUriString(apiUrl).build().toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBasicAuth(username, password);

        //LOT_ID AND INN SEND TO MUHAMMADALI'S SERVER WITH BASIC AUTH
        logger.info("Send to Muhammadali's server inn: "+ createTenderDTO.getInn()+" lod_id: "+ createTenderDTO.getLotId());
        HttpEntity<CreateTenderDTO> requestEntity = new HttpEntity<>(createTenderDTO, headers);
        TempTenderDTO tempTenderDTO = restTemplate.postForObject(uri, requestEntity, TempTenderDTO.class);

        // EXTRACT ROLE AND CODE FROM THE RESPONSE DTO OBJECT
        String role = Objects.requireNonNull(tempTenderDTO).getResult().getData().getRole();
        String code = tempTenderDTO.getResult().getData().getCode();

        return ApiResult.successResponse(new TenderAuthDTO(createTenderDTO.getLotId(), createTenderDTO.getInn(), role, code));
    }

    private List<TenderInfoDTO> saveTender(Smeta smeta, List<TenderInfoAddDTO> smetaDtoList) {
        if (Objects.isNull(smetaDtoList))
            throw RestException.restThrow("Smeta array must not be empty", HttpStatus.BAD_REQUEST);
        return smetaDtoList.stream().map(smetaDto -> {
            Lot lot = lotRepository.save(mapTenderAddDTOToTender(smetaDto, smeta));
            return mapTenderToTenderDTO(lot);}).toList();
    }

    private TenderInfoDTO mapTenderToTenderDTO(Lot lot) {
        return TenderInfoDTO.builder()
                .ed_ism(lot.getEdIsm())
                .id(lot.getId())
                .kod_snk(lot.getKodSnk())
                .type(lot.getType())
                .price(lot.getPrice())
                .name(lot.getName())
                .norma(lot.getNorma())
                .rashod(lot.getRashod())
                .summa(lot.getSumma())
                .build();
    }

    private Lot mapTenderAddDTOToTender(TenderInfoAddDTO tenderInfo, Smeta smeta) {
        return Lot.builder()
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
                .build();
    }

    private SmetaDTO mapSmetaToSmetaDTO(Smeta smeta, List<TenderInfoDTO> tenderInfoDTOS){
        return new SmetaDTO(smeta.getId(), smeta.getSmName(), smeta.getSmNum(), tenderInfoDTOS);
    }

    private ObjectDTO mapObjectToObjectDTO(Object object, List<SmetaDTO> smetaDTOS){
        return new ObjectDTO(object.getId(),object.getObName(), object.getObNum(), smetaDTOS);
    }
}
