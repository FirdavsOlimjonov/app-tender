package uz.mc.apptender.controller;

import org.springframework.web.bind.annotation.*;
import uz.mc.apptender.payload.ApiResult;
import uz.mc.apptender.payload.CreateTenderDTO;
import uz.mc.apptender.payload.StroyAddDTO;
import uz.mc.apptender.payload.StroyDTO;
import uz.mc.apptender.utils.RestConstants;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RequestMapping(TenderController.ADDRESS_BASE_PATH)
public interface TenderControllerInterface {

    String ADDRESS_BASE_PATH = RestConstants.BASE_PATH+"tender";

    @PostMapping("/add")
    ApiResult<StroyDTO> add(@RequestBody @Valid StroyAddDTO stroyAddDTO);

    @PostMapping("/create")
    ApiResult<?> createTender(@RequestBody @Valid CreateTenderDTO createTenderDTO);

    @GetMapping("/get")
    ApiResult<?> getForOfferor(@RequestParam("inn") Long innOfferor,@RequestParam("lot_id") Long lotId );

    @GetMapping("/{lotId}")
    ApiResult<?> generateExcel(@PathVariable Long lotId, HttpServletResponse httpServletResponse);
}
