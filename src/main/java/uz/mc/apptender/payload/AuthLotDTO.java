package uz.mc.apptender.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuthLotDTO {
    private String role;
    private long userId;
    private long lotId;
    private int status;
    private boolean customerCanChange;
    private boolean offerorCanChange;
}
