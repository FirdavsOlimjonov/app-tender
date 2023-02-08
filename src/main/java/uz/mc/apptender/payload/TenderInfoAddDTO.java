package uz.mc.apptender.payload;

import lombok.Getter;

import javax.validation.constraints.*;
import uz.mc.apptender.utils.MessageConstants;

import java.math.BigDecimal;

@Getter
public class TenderInfoAddDTO {
    @NotNull(message = MessageConstants.MUST_NOT_BE_NULL_ID)
    private Integer id;

//    @NotNull(message = MessageConstants.MUST_NOT_BE_NULL_CATEGORY_ID)
//    private Integer catId;
//
//    @NotNull(message = MessageConstants.MUST_NOT_BE_NULL_USER_ID)
//    private Integer userId;

    @NotNull(message = MessageConstants.MUST_NOT_BE_NULL_TYPE)
    private Integer type;

//    @NotNull(message = MessageConstants.MUST_NOT_BE_NULL_KOD_SNK)
//    @NotBlank(message = MessageConstants.MUST_NOT_BE_BLANK_KOD_SNK)
    private String kodSnk;

    @NotBlank(message = MessageConstants.MUST_NOT_BE_BLANK_NAME)
    @NotNull(message = MessageConstants.MUST_NOT_BE_NULL_NAME)
    private String name;

    @NotBlank(message = MessageConstants.MUST_NOT_BE_BLANK_EDISM)
    @NotNull(message = MessageConstants.MUST_NOT_BE_NULL_EDISM)
    private String edIsm;

    @NotNull(message = MessageConstants.MUST_NOT_BE_NULL_NORMA)
    private Double norma;

    @NotNull(message = MessageConstants.MUST_NOT_BE_NULL_RASHOD)
    private BigDecimal rashod;

    @NotNull(message = MessageConstants.MUST_NOT_BE_NULL_PRICE)
    private BigDecimal price;

    @NotNull(message = MessageConstants.MUST_NOT_BE_NULL_SUMMA)
    private BigDecimal summa;
}
