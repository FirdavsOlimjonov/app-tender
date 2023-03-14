package uz.mc.apptender.payload;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthLotDTO {
    private String role;
    private long userId;
}
