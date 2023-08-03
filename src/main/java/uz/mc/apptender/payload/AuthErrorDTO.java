package uz.mc.apptender.payload;

import lombok.Getter;
import lombok.Setter;

import javax.annotation.processing.Generated;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class AuthErrorDTO {
    private List<ErrorDTO> error;
    private int code;

    @Getter
    @Setter
    public static class ErrorDTO {
        private Map<String, List<String>> fieldErrors;
    }

}
