package uz.mc.apptender.service;

import uz.mc.apptender.payload.ApiResult;
import uz.mc.apptender.payload.StroyAddDTO;
import uz.mc.apptender.payload.StroyDTO;

public interface TenderService {

    ApiResult<StroyDTO> add(StroyAddDTO stroyAddDTO);

}
