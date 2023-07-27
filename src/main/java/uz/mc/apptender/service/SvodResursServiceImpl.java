package uz.mc.apptender.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.mc.apptender.payload.ApiResult;
import uz.mc.apptender.payload.SvodResursDAO;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SvodResursServiceImpl implements SvodResursService{

    @Override
    public ApiResult<List<SvodResursDAO>> get(long lot_id) {

        return null;
    }

    @Override
    public ApiResult<?> update(long lot_id, SvodResursDAO svodResursDAO) {

        return null;
    }
}
