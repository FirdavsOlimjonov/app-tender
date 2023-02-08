package uz.mc.apptender.service;

import uz.mc.apptender.payload.ApiResult;
import uz.mc.apptender.payload.TenderInfoAddDTO;
import uz.mc.apptender.payload.TenderInfoDTO;

import java.util.List;

public interface TenderService {

    ApiResult<List<TenderInfoDTO>> add(TenderInfoAddDTO[] tenderInfoAddDTO);

}
