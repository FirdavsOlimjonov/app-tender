package uz.mc.apptender.service;

import uz.mc.apptender.payload.ApiResult;
import uz.mc.apptender.payload.SvodResursDAO;

import java.util.List;

public interface SvodResursService {
    ApiResult<List<SvodResursDAO>> get(long lot_id);

    ApiResult<?> update(long lot_id, SvodResursDAO svodResursDAO);
}
