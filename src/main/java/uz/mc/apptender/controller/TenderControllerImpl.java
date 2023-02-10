package uz.mc.apptender.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import uz.mc.apptender.payload.ApiResult;
import uz.mc.apptender.payload.CoverAdd;
import uz.mc.apptender.payload.CoverDTO;
import uz.mc.apptender.payload.TenderInfoDTO;
import uz.mc.apptender.service.TenderService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TenderControllerImpl implements TenderController{
    private final TenderService tenderService;

    @Override
    public ApiResult<CoverDTO> add(CoverAdd coverAdd) {
        return tenderService.add(coverAdd);
    }
}
