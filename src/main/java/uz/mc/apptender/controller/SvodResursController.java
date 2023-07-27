package uz.mc.apptender.controller;

import org.springframework.web.bind.annotation.*;
import uz.mc.apptender.payload.ApiResult;
import uz.mc.apptender.payload.SvodResursDAO;
import uz.mc.apptender.utils.RestConstants;

import javax.validation.Valid;
import java.util.List;

@RequestMapping(SvodResursController.ADDRESS_BASE_PATH)
public interface SvodResursController {
    String ADDRESS_BASE_PATH = RestConstants.BASE_PATH + "tender";

    @GetMapping("/get-svod-info/{lot_id}")
    ApiResult<List<SvodResursDAO>> get(@PathVariable long lot_id);

    @PutMapping("/update-svod-info/{lot_id}")
    ApiResult<?> update(@PathVariable long lot_id,
                        @RequestBody @Valid SvodResursDAO svodResursDAO);

}
