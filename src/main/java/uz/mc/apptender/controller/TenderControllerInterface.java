package uz.mc.apptender.controller;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.mc.apptender.payload.ApiResult;
import uz.mc.apptender.payload.CreateTenderDTO;
import uz.mc.apptender.payload.StroyAddDTO;
import uz.mc.apptender.payload.StroyDTO;
import uz.mc.apptender.utils.RestConstants;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RequestMapping(TenderControllerInterface.ADDRESS_BASE_PATH)
public interface TenderControllerInterface {

    String ADDRESS_BASE_PATH = RestConstants.BASE_PATH+"tender";

    @PostMapping("/add")
    ApiResult<StroyDTO> add(@RequestBody @Valid StroyAddDTO stroyAddDTO);

    @PostMapping("/create")
    ApiResult<?> createTender(@RequestBody @Valid CreateTenderDTO createTenderDTO);

    @GetMapping("/get")
    ApiResult<?> getSmeta(@RequestParam("inn") Long inn,@RequestParam("lot_id") Long lotId );

    @GetMapping("/{lotId}")
    ResponseEntity<Resource> generateExcel(@PathVariable Long lotId);
}
