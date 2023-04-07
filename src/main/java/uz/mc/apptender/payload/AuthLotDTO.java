package uz.mc.apptender.payload;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthLotDTO {
    private String role;
    private long userId;
    private int status;
    private boolean customerCanChange;
    private boolean offerorCanChange;
}
