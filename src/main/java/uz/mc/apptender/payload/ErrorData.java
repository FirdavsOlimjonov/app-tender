package uz.mc.apptender.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorData {
    //USERGA BORADIGAN XABAR
    private String errorMsg;

    //XATOLIK KODI
    private Integer errorCode;

    //QAYSI FIELD XATO EKANLIGI
    private String fieldName;

    public ErrorData(String errorMsg, Integer errorCode) {
        this.errorMsg = errorMsg;
        this.errorCode = errorCode;
    }
}
