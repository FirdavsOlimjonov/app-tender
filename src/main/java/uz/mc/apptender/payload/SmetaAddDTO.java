package uz.mc.apptender.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import uz.mc.apptender.utils.MessageConstants;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
public class SmetaAddDTO {
    @NotBlank(message = MessageConstants.MUST_NOT_BE_BLANK_NAME)
    @NotNull(message = MessageConstants.MUST_NOT_BE_NULL_NAME)
    @JsonProperty("sm_name")
    private String smName;

    @NotBlank(message = MessageConstants.MUST_NOT_BE_BLANK_NUMBER)
    @NotNull(message = MessageConstants.MUST_NOT_BE_NULL_NUMBER)
    @JsonProperty("sm_num")
    private String smNum;

    @NotNull(message = MessageConstants.SMETA_NOT_BE_NULL)
    @NotEmpty(message = MessageConstants.SMETA_NOT_BE_EMPTY)
    @JsonProperty("smeta")
    private List<TenderInfoAddDTO> smeta;
}
