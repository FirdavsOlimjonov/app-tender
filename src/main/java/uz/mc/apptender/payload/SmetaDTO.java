package uz.mc.apptender.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import uz.mc.apptender.utils.MessageConstants;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class SmetaDTO {
    private Integer id;
    @JsonProperty("sm_name")
    private String smName;
    @JsonProperty("sm_num")
    private String smNum;
    @JsonProperty("smeta")
    private List<TenderInfoDTO> smeta;
}
