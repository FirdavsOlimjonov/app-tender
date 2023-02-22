package uz.mc.apptender.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import uz.mc.apptender.modules.Smeta;
import uz.mc.apptender.utils.MessageConstants;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class ObjectAddDTO {
    @NotBlank(message = MessageConstants.MUST_NOT_BE_BLANK_NAME)
    @NotNull(message = MessageConstants.MUST_NOT_BE_NULL_NAME)
    @JsonProperty("ob_name")
    private String obName;

    @NotBlank(message = MessageConstants.MUST_NOT_BE_BLANK_NUMBER)
    @NotNull(message = MessageConstants.MUST_NOT_BE_NULL_NUMBER)
    @JsonProperty("ob_num")
    private String obNum;

    @NotNull(message = MessageConstants.SMETA_ARRAY_NOT_BE_NULL)
    @NotEmpty(message = MessageConstants.SMETA_ARRAY_NOT_BE_EMPTY)
    @JsonProperty("sm_array")
    private List<SmetaAddDTO> smArray;
}
