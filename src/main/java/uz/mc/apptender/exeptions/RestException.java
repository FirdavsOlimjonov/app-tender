package uz.mc.apptender.exeptions;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;
import uz.mc.apptender.payload.ErrorData;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class RestException extends RuntimeException {

    private String userMsg;
    private HttpStatus status;
    private List<ErrorData> errors;

    private String resourceName;
    private String fieldName;
    private Object fieldValue;

    public RestException(String userMsg, HttpStatus status) {
        super(userMsg);
        this.userMsg = userMsg;
        this.status = status;
    }

    private RestException(HttpStatus status, List<ErrorData> errors) {
        this.status = status;
        this.errors = errors;
    }

    public static RestException restThrow(String userMsg, HttpStatus httpStatus) {
        return new RestException(userMsg, httpStatus);
    }

    public static RestException restThrow(String userMsg) {
        return new RestException(userMsg, HttpStatus.BAD_REQUEST);
    }

    public static RestException restThrow(List<ErrorData> errors, HttpStatus status) {
        return new RestException(status, errors);
    }
}
