package uz.mc.apptender.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.mc.apptender.modules.Tender;
import uz.mc.apptender.payload.ApiResult;
import uz.mc.apptender.payload.TenderInfoAddDTO;
import uz.mc.apptender.payload.TenderInfoDTO;
import uz.mc.apptender.repositories.TenderRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TenderServiceImpl implements TenderService {
    private final TenderRepository tenderRepository;

    private Integer tenderId;

    @Override
    public ApiResult<List<TenderInfoDTO>> add(TenderInfoAddDTO[] tenderInfoAddDTO) {
        Optional<Tender> tenderOptional = tenderRepository.findTopByOrderByCreatedAtDesc();
        tenderId = tenderOptional.map(value -> value.getTenderId() + 1).orElse(1);
        List<TenderInfoDTO> dtoList = new ArrayList<>();

        for (TenderInfoAddDTO tenderInfo : tenderInfoAddDTO) {
            Tender tender = mapTenderAddDTOToTender(tenderInfo);
            tenderRepository.save(tender);
            dtoList.add(mapTenderToTenderDTO(tender));
        }

        return ApiResult.successResponse(dtoList);
    }

    private TenderInfoDTO mapTenderToTenderDTO(Tender tender) {
        return TenderInfoDTO.builder()
                .tenderId(tenderId)
                .catId(tender.getCatId())
                .userId(tender.getUserId())
                .edIsm(tender.getEdIsm())
                .id(tender.getId())
                .kodSnk(tender.getKodSnk())
                .type(tender.getType())
                .price(tender.getPrice())
                .name(tender.getName())
                .norma(tender.getNorma())
                .rashod(tender.getRashod())
                .summa(tender.getSumma())
                .build();
    }

    private Tender mapTenderAddDTOToTender(TenderInfoAddDTO tenderInfo) {
        return Tender.builder()
                .tenderId(tenderId)
                .catId(1)
                .userId(1)
                .edIsm(tenderInfo.getEdIsm())
                .id(tenderInfo.getId())
                .kodSnk(tenderInfo.getKodSnk())
                .type(tenderInfo.getType())
                .price(tenderInfo.getPrice())
                .name(tenderInfo.getName())
                .norma(tenderInfo.getNorma())
                .rashod(tenderInfo.getRashod())
                .summa(tenderInfo.getSumma())
                .build();
    }
}
