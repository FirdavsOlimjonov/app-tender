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
public class ObjectDTO {
    private Integer id;
    @JsonProperty("ob_name")
    private String obName;
    @JsonProperty("ob_num")
    private String obNum;
    @JsonProperty("sm_array")
    private List<SmetaDTO> smArray;
}
