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
public class CreateTenderDTO {
    @NotNull(message = MessageConstants.MUST_NOT_BE_NULL_LOT_ID)
    @NotBlank(message = MessageConstants.MUST_NOT_BE_BLANK_LOT_ID)
    @JsonProperty("lot_id")
    private String lotId;

    @NotBlank(message = MessageConstants.MUST_NOT_BE_BLANK_PHONE_NUMBER)
    @NotNull(message = MessageConstants.MUST_NOT_BE_NULL_PHONE_NUMBER)
    @JsonProperty("inn")
    private String inn;
}
