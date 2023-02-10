package uz.mc.apptender.payload;

import lombok.Builder;
import lombok.Getter;
import uz.mc.apptender.utils.MessageConstants;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Builder
public class CoverDTO {
    private List<TenderInfoDTO> mtbJson;
}
