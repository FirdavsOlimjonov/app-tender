package uz.mc.apptender.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uz.mc.apptender.modules.Object;
import uz.mc.apptender.modules.Smeta;
import uz.mc.apptender.modules.Stroy;
import uz.mc.apptender.modules.Tender;
import uz.mc.apptender.payload.*;
import uz.mc.apptender.repositories.ObjectRepository;
import uz.mc.apptender.repositories.SmetaRepository;
import uz.mc.apptender.repositories.StroyRepository;
import uz.mc.apptender.repositories.TenderRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TenderServiceImpl implements TenderService {
    private final TenderRepository tenderRepository;
    private final StroyRepository stroyRepository;
    private final ObjectRepository objectRepository;
    private final SmetaRepository smetaRepository;
    private static final Logger logger = LoggerFactory.getLogger(TenderServiceImpl.class);
    private Integer tenderId;

    @Override
    public ApiResult<StroyDTO> add(StroyAddDTO stroyAddDTO) {
        Optional<Stroy> tenderOptional = stroyRepository.findTopByOrderByCreatedAtDesc();
        tenderId = tenderOptional.map(value -> value.getTenderId() + 1).orElse(1);
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

        return ApiResult.successResponse(new StroyDTO(stroy.getId(), stroy.getStrName(), stroy.getTenderId(), objectDTOList));
    }

    private List<TenderInfoDTO> saveTender(Smeta smeta, List<TenderInfoAddDTO> smetaDtoList) {
        return smetaDtoList.stream().map(smetaDto -> {
            Tender tender = tenderRepository.save(mapTenderAddDTOToTender(smetaDto, smeta));
            return mapTenderToTenderDTO(tender);}).toList();
    }

    private TenderInfoDTO mapTenderToTenderDTO(Tender tender) {
        return TenderInfoDTO.builder()
                .cat_id(tender.getCatId())
                .user_id(tender.getUserId())
                .ed_ism(tender.getEdIsm())
                .id(tender.getId())
                .kod_snk(tender.getKodSnk())
                .type(tender.getType())
                .price(tender.getPrice())
                .name(tender.getName())
                .norma(tender.getNorma())
                .rashod(tender.getRashod())
                .summa(tender.getSumma())
                .build();
    }

    private Tender mapTenderAddDTOToTender(TenderInfoAddDTO tenderInfo, Smeta smeta) {
        return Tender.builder()
                .catId(1)
                .userId(1)
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
