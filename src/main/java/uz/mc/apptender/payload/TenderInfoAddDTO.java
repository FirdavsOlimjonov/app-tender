package uz.mc.apptender.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import uz.mc.apptender.utils.MessageConstants;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class TenderInfoAddDTO {

    @JsonProperty("SM_ID")
    private Long smId;

    @NotNull(message = MessageConstants.MUST_NOT_BE_NULL_ID)
    @JsonProperty("NUM")
    private Integer num;

    @NotNull(message = MessageConstants.MUST_NOT_BE_NULL_TYPE)
    @JsonProperty("ROW_TYPE")
    private Integer rowType;

    @NotNull(message = MessageConstants.MUST_NOT_BE_NULL_TYPE)
    @JsonProperty("OPRED")
    private Integer opred;

    //    @NotNull(message = MessageConstants.MUST_NOT_BE_NULL_KOD_SNK)
//    @NotBlank(message = MessageConstants.MUST_NOT_BE_BLANK_KOD_SNK)
    @JsonProperty("KOD_SNK")
    private String kod_snk;

    @NotBlank(message = MessageConstants.MUST_NOT_BE_BLANK_NAME)
    @JsonProperty("NAME")
    private String name;

    @NotBlank(message = MessageConstants.MUST_NOT_BE_BLANK_EDISM)
    @NotNull(message = MessageConstants.MUST_NOT_BE_NULL_EDISM)
    @JsonProperty("ED_ISM")
    private String ed_ism;

    @NotNull(message = MessageConstants.MUST_NOT_BE_NULL_NORMA)
    @JsonProperty("NORMA")
    private Double norma;

    @NotNull(message = MessageConstants.MUST_NOT_BE_NULL_RASHOD)
    @JsonProperty("RASHOD")
    private Double rashod;

    @NotNull(message = MessageConstants.MUST_NOT_BE_NULL_PRICE)
    @JsonProperty("PRICE")
    private BigDecimal price;

    @NotNull(message = MessageConstants.MUST_NOT_BE_NULL_SUMMA)
    @JsonProperty("SUMMA")
    private BigDecimal summa;

    @JsonProperty("res_array")
    private List<TenderInfoAddDTO> resArray;
}
