package uz.mc.apptender.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.mc.apptender.payload.ApiResult;
import uz.mc.apptender.payload.TenderInfoAddDTO;
import uz.mc.apptender.payload.TenderInfoDTO;
import uz.mc.apptender.service.TenderService;
import uz.mc.apptender.utils.RestConstants;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class TenderControllerImpl implements TenderController{
    private final TenderService tenderService;

    @Override
    public ApiResult<List<TenderInfoDTO>> add(@Valid TenderInfoAddDTO[] tenderInfoAddDTO) {
        return tenderService.add(tenderInfoAddDTO);
    }
}
