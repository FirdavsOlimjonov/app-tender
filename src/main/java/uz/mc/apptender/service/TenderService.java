package uz.mc.apptender.service;

import uz.mc.apptender.payload.ApiResult;
import uz.mc.apptender.payload.CoverAdd;
import uz.mc.apptender.payload.CoverDTO;

public interface TenderService {

    ApiResult<CoverDTO> add(CoverAdd coverAdd);

}
