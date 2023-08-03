package uz.mc.apptender.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import uz.mc.apptender.utils.MessageConstants;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SvodResursDAO {
    @JsonProperty("ID")
    private Long id;

    @NotNull(message = MessageConstants.MUST_NOT_BE_NULL_ID)
    @JsonProperty("NUM")
    private Integer num;

    @NotNull(message = MessageConstants.MUST_NOT_BE_NULL_KODV)
    @JsonProperty("KODV")
    private Integer kodv;

    @NotNull(message = MessageConstants.MUST_NOT_BE_NULL_TIN)
    @JsonProperty("TIP")
    private Integer tip;

    @NotNull(message = MessageConstants.MUST_NOT_BE_BLANK_KODR)
    @JsonProperty("KODR")
    private String kodr;

    @NotBlank(message = MessageConstants.MUST_NOT_BE_BLANK_KODM)
    @JsonProperty("KODM")
    private String kodm;

    @NotBlank(message = MessageConstants.MUST_NOT_BE_BLANK_KODINAME)
    @JsonProperty("KODINAME")
    private String kodiName;

    @NotBlank(message = MessageConstants.MUST_NOT_BE_BLANK_NAME)
    @JsonProperty("NAME")
    private String name;

    @NotNull(message = MessageConstants.MUST_NOT_BE_NULL_KOL)
    @JsonProperty("KOL")
    private Double kol;

    @NotNull(message = MessageConstants.MUST_NOT_BE_NULL_PRICE)
    @JsonProperty("PRICE")
    private BigDecimal price;

    @NotNull(message = MessageConstants.MUST_NOT_BE_NULL_SUMMA)
    @JsonProperty("SUMMA")
    private BigDecimal summa;
}
