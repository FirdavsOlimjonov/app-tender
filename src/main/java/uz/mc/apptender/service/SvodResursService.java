package uz.mc.apptender.service;

import uz.mc.apptender.payload.ApiResult;
import uz.mc.apptender.payload.SvodResursDAO;
import uz.mc.apptender.payload.SvodResursUpdate;

import java.util.List;

public interface SvodResursService {
    ApiResult<List<SvodResursDAO>> get(long lot_id, long inn);

    ApiResult<?> update(SvodResursUpdate svodResursUpdate);

}
