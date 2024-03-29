package uz.mc.apptender.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import uz.mc.apptender.modules.enums.RoleEnum;
import uz.mc.apptender.utils.MessageConstants;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@Setter
@Builder
public class TenderInfoDTO {
    @JsonProperty("SM_ID")
    private Long smId;

    @JsonProperty("NUM")
    private Integer num;

    @JsonProperty("ROW_TYPE")
    private Integer rowType;

    @JsonProperty("OPRED")
    private Integer opred;

    @JsonProperty("USER_ID")
    private Long userId;

    @JsonProperty("KOD_SNK")
    private String kod_snk;

    @JsonProperty("NAME")
    private String name;

    @JsonProperty("ED_ISM")
    private String ed_ism;

    @JsonProperty("NORMA")
    private Double norma;

    @JsonProperty("RASHOD")
    private Double rashod;

    @JsonProperty("PRICE")
    private BigDecimal price;

    @JsonProperty("SUMMA")
    private BigDecimal summa;

    @JsonProperty("res_array")
    private List<TenderInfoDTO> resArray;
}
