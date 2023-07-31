package uz.mc.apptender.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import uz.mc.apptender.payload.ApiResult;
import uz.mc.apptender.payload.CreateTenderDTO;
import uz.mc.apptender.payload.StroyAddDTO;
import uz.mc.apptender.payload.StroyDTO;
import uz.mc.apptender.service.TenderService;
import uz.mc.apptender.utils.ExcelGenerate;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequiredArgsConstructor
public class TenderController implements TenderControllerInterface {
    private final TenderService tenderService;
    private final ExcelGenerate excelGenerate;

    @Override
    public ApiResult<StroyDTO> add(StroyAddDTO stroyAddDTO) {
        return tenderService.add(stroyAddDTO);
    }

    @Override
    public ApiResult<?> createTender(CreateTenderDTO createTenderDTO) {
        return tenderService.createTender(createTenderDTO);
    }

    @Override
    public ApiResult<?> getSmeta(Long innOfferor, Long lotId) {
        return tenderService.getSmeta(innOfferor, lotId);
    }

    @Override
    public ResponseEntity<Resource> generateExcel(Long lotId) {
         return excelGenerate.generateExcel(lotId);
    }

}
