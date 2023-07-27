package uz.mc.apptender.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import uz.mc.apptender.payload.ApiResult;
import uz.mc.apptender.payload.SvodResursDAO;
import uz.mc.apptender.service.SvodResursService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class SvodResursControllerImpl implements SvodResursController {
    private final SvodResursService svodResursService;

    @Override
    public ApiResult<List<SvodResursDAO>> get(long lot_id) {
        return svodResursService.get(lot_id);
    }

    @Override
    public ApiResult<?> update(long lot_id, SvodResursDAO svodResursDAO) {
        return svodResursService.update(lot_id, svodResursDAO);
    }
}
