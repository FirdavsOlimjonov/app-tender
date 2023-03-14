package uz.mc.apptender.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import uz.mc.apptender.utils.MessageConstants;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StroyDTO {
    private Integer id;
    @JsonProperty("str_name")
    private String strName;
    @JsonProperty("tender_id")
    private Integer tenderId;
    @JsonProperty("lot_id")
    private Long lotId;
    @JsonProperty("inn")
    private Long inn;
    @JsonProperty("role")
    private String role;
    @JsonProperty("ob_array")
    private List<ObjectDTO> obArray;
}
