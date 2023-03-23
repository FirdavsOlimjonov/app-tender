package uz.mc.apptender.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uz.mc.apptender.payload.ApiResult;
import uz.mc.apptender.payload.CreateTenderDTO;
import uz.mc.apptender.payload.StroyAddDTO;
import uz.mc.apptender.payload.StroyDTO;
import uz.mc.apptender.service.TenderService;

@RestController
@RequiredArgsConstructor
public class TenderControllerImpl implements TenderController {
    private final TenderService tenderService;

    @Override
    public ApiResult<StroyDTO> add(StroyAddDTO stroyAddDTO) {
        return tenderService.add(stroyAddDTO);
    }

    @Override
    public ApiResult<?> createTender(CreateTenderDTO createTenderDTO) {
        return tenderService.createTender(createTenderDTO);
    }

    @Override
    public ApiResult<?> getForOfferor(Long innOfferor, Long innCustomer, Long lotId) {
        return tenderService.getForOfferor(innOfferor, innCustomer, lotId);
    }

}
