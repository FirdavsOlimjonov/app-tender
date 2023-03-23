package uz.mc.apptender.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import uz.mc.apptender.modules.enums.RoleEnum;
import uz.mc.apptender.utils.MessageConstants;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
public class StroyAddDTO {
    private Integer id;

    @NotNull(message = MessageConstants.MUST_NOT_BE_NULL_NAME)
    @NotBlank(message = MessageConstants.MUST_NOT_BE_BLANK_NAME)
    @JsonProperty("str_name")
    private String strName;

    @NotNull(message = MessageConstants.MUST_NOT_BE_NULL_LOT_ID)
    @JsonProperty("lot_id")
    private Long lotId;

    @NotNull(message = MessageConstants.MUST_NOT_BE_NULL_PHONE_NUMBER)
    @JsonProperty("inn")
    private Long inn;

    @NotNull(message = MessageConstants.MTB_JSON_NOT_BE_NULL)
    @NotEmpty(message = MessageConstants.MTB_JSON_NOT_BE_EMPTY)
    @JsonProperty("ob_array")
    private List<ObjectAddDTO> obArray;
}
