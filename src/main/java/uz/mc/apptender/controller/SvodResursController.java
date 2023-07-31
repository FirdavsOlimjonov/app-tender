package uz.mc.apptender.controller;

import org.springframework.web.bind.annotation.*;
import uz.mc.apptender.payload.ApiResult;
import uz.mc.apptender.payload.SvodResursDAO;
import uz.mc.apptender.payload.SvodResursUpdate;
import uz.mc.apptender.utils.RestConstants;

import javax.validation.Valid;
import java.util.List;

@RequestMapping(SvodResursController.ADDRESS_BASE_PATH)
public interface SvodResursController {
    String ADDRESS_BASE_PATH = RestConstants.BASE_PATH + "tender";

    @GetMapping("/get-svod-info")
    ApiResult<List<SvodResursDAO>> get(@RequestParam("lot_id") long lot_id, @RequestParam("inn") long inn );

    @PutMapping("/update-svod-info")
    ApiResult<?> update(@RequestBody @Valid SvodResursUpdate svodResursUpdate);

}
