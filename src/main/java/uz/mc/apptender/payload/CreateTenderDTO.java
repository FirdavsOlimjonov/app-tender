package uz.mc.apptender.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.mc.apptender.utils.MessageConstants;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateTenderDTO {
    @NotNull(message = MessageConstants.MUST_NOT_BE_NULL_LOT_ID)
    @JsonProperty("lot_id")
    private Long lotId;

    @NotNull(message = MessageConstants.MUST_NOT_BE_NULL_PHONE_NUMBER)
    @JsonProperty("inn")
    private Long inn;
}
