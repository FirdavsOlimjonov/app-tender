package uz.mc.apptender.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import javax.validation.constraints.*;

import lombok.Setter;
import uz.mc.apptender.utils.MessageConstants;

import java.math.BigDecimal;

@Getter
@Setter
public class TenderInfoAddDTO {
    //    @NotNull(message = MessageConstants.MUST_NOT_BE_NULL_CATEGORY_ID)
//    private Integer catId;
//
//    @NotNull(message = MessageConstants.MUST_NOT_BE_NULL_USER_ID)
//    private Integer userId;

    @NotNull(message = MessageConstants.MUST_NOT_BE_NULL_ID)
    @JsonProperty("ID")
    private Integer id;

    @NotNull(message = MessageConstants.MUST_NOT_BE_NULL_TYPE)
    @JsonProperty("TYPE")
    private Integer type;

    //    @NotNull(message = MessageConstants.MUST_NOT_BE_NULL_KOD_SNK)
//    @NotBlank(message = MessageConstants.MUST_NOT_BE_BLANK_KOD_SNK)
    @JsonProperty("KOD_SNK")
    private String kod_snk;

    @NotBlank(message = MessageConstants.MUST_NOT_BE_BLANK_NAME)
    @NotNull(message = MessageConstants.MUST_NOT_BE_NULL_NAME)
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
    private BigDecimal rashod;

    @NotNull(message = MessageConstants.MUST_NOT_BE_NULL_PRICE)
    @JsonProperty("PRICE")
    private BigDecimal price;

    @NotNull(message = MessageConstants.MUST_NOT_BE_NULL_SUMMA)
    @JsonProperty("SUMMA")
    private BigDecimal summa;
}
