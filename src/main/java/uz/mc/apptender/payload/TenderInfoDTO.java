package uz.mc.apptender.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import uz.mc.apptender.modules.enums.RoleEnum;
import uz.mc.apptender.utils.MessageConstants;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Getter
//@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@Builder
public class TenderInfoDTO {
    @JsonProperty("SM_ID")
    private int smId;
    @JsonProperty("ID")
    private int id;
    @JsonProperty("TYPE")
    private int type;
    @JsonProperty("USER_ID")
    private long userId;
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
}
