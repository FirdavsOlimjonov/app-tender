package uz.mc.apptender.payload;

import lombok.Getter;
import uz.mc.apptender.utils.MessageConstants;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
public class CoverAdd {
    @NotNull(message = MessageConstants.MTB_JSON_NOT_BE_NULL)
    @NotEmpty(message = MessageConstants.MTB_JSON_NOT_BE_EMPTY)
    private List<TenderInfoAddDTO> mtbJson;
}
