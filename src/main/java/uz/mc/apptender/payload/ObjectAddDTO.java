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
    private Integer id;

    @JsonProperty("ob_name")
    private String obName;

    @JsonProperty("ob_num")
    private String obNum;

    @JsonProperty("sm_array")
    private List<SmetaAddDTO> smArray;
}
