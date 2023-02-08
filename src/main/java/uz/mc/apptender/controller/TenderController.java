package uz.mc.apptender.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import uz.mc.apptender.payload.ApiResult;
import uz.mc.apptender.payload.TenderInfoAddDTO;
import uz.mc.apptender.payload.TenderInfoDTO;
import uz.mc.apptender.utils.RestConstants;

import javax.validation.Valid;
import java.util.List;

@RequestMapping(TenderController.ADDRESS_BASE_PATH)
public interface TenderController {

    String ADDRESS_BASE_PATH = RestConstants.BASE_PATH+"tender";
    @PostMapping("/add")
    ApiResult<List<TenderInfoDTO>> add(@RequestBody @Valid TenderInfoAddDTO[] tenderInfoAddDTO);
}
