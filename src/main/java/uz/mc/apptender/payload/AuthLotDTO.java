package uz.mc.apptender.payload;

import lombok.Getter;
import lombok.Setter;
import uz.mc.apptender.modules.enums.RoleEnum;

@Getter
@Setter
public class AuthLotDTO {
    private RoleEnum role;
    private long userId;
}
