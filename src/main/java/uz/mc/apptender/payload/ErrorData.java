package uz.mc.apptender.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorData {
    //USERGA BORADIGAN XABAR
    private String errorMsg;

    //XATOLIK KODI
    private int errorCode;

    //QAYSI FIELD XATO EKANLIGI
    private String fieldName;

    private Map<String, List<String>> fieldErrors;

    public ErrorData(int errorCode, Map<String, List<String>> fieldErrors) {
        this.errorCode = errorCode;
        this.fieldErrors = fieldErrors;
    }

    public ErrorData(String errorMsg, Integer errorCode) {
        this.errorMsg = errorMsg;
        this.errorCode = errorCode;
    }

    public ErrorData(String errorMessage, int value, String field) {
        this.errorMsg = errorMessage;
        this.errorCode = value;
        this.fieldName = field;
    }
}
