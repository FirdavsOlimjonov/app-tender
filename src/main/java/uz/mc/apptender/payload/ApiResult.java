package uz.mc.apptender.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResult<T> implements Serializable {
    private Boolean success = false;
    private String message;
    private T data;
    private List<ErrorData> errors;

    //RESPONSE WITH BOOLEAN (SUCCESS OR FAIL)
    private ApiResult(Boolean success) {
        this.success = success;
    }

    //SUCCESS RESPONSE WITH DATA
    private ApiResult(T data, Boolean success) {
        this.data = data;
        this.success = success;
    }

    //SUCCESS RESPONSE WITH DATA AND MESSAGE
    private ApiResult(T data, Boolean success, String message) {
        this.data = data;
        this.success = success;
        this.message = message;
    }

    //SUCCESS RESPONSE WITH MESSAGE
    private ApiResult(String message) {
        this.message = message;
        this.success = Boolean.TRUE;
    }

    //ERROR RESPONSE WITH MESSAGE AND ERROR CODE
    private ApiResult(String errorMsg, Integer errorCode) {
        this.success = false;
        this.errors = Collections.singletonList(new ErrorData(errorMsg, errorCode));
    }

    //ERROR RESPONSE WITH ERROR DATA LIST
    private ApiResult(List<ErrorData> errors) {
        this.success = false;
        this.errors = errors;
    }

    public static <E> ApiResult<E> successResponse(E data) {
        return new ApiResult<>(data, true);
    }

    public static <E> ApiResult<E> successResponse(E data, String message) {
        return new ApiResult<>(data, true, message);
    }

    public static <E> ApiResult<E> successResponse() {
        return new ApiResult<>(true);
    }

    public static ApiResult<String> successResponse(String message) {
        return new ApiResult<>(message);
    }


    public static ApiResult<ErrorData> errorResponse(String errorMsg, Integer errorCode) {
        return new ApiResult<>(errorMsg, errorCode);
    }


    public static ApiResult<ErrorData> errorResponse(List<ErrorData> errors) {
        return new ApiResult<>(errors);
    }
}
