package uz.mc.apptender.service;

import uz.mc.apptender.payload.ApiResult;
import uz.mc.apptender.payload.CreateTenderDTO;
import uz.mc.apptender.payload.StroyAddDTO;
import uz.mc.apptender.payload.StroyDTO;

public interface TenderService {

    ApiResult<StroyDTO> add(StroyAddDTO stroyAddDTO);

    ApiResult<?> createTender(CreateTenderDTO createTenderDTO);

    ApiResult<?> getForOfferor(Long inn, Long lotId);
}
