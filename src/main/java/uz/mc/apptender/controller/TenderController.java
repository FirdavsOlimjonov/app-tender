package uz.mc.apptender.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import uz.mc.apptender.payload.ApiResult;
import uz.mc.apptender.payload.StroyAddDTO;
import uz.mc.apptender.payload.StroyDTO;
import uz.mc.apptender.utils.RestConstants;

import javax.validation.Valid;

@RequestMapping(TenderController.ADDRESS_BASE_PATH)
public interface TenderController {

    String ADDRESS_BASE_PATH = RestConstants.BASE_PATH+"tender";
    @PostMapping("/add")
    ApiResult<StroyDTO> add(@RequestBody @Valid StroyAddDTO stroyAddDTO);
}
