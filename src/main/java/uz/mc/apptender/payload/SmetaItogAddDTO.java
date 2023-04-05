package uz.mc.apptender.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Setter
@Getter
public class SmetaItogAddDTO {

    @JsonProperty("ZATR_TRUD")
    @NotNull(message = "ZATR_TRUD must not be null")
    private Double zatrTrud;

    @JsonProperty("SUMMA_ZP")
    @NotNull(message = "SUMMA_ZP must not be null")
    private BigDecimal summaZp;

    @JsonProperty("SUMMA_EXP")
    @NotNull(message = "SUMMA_EXP must not be null")
    private BigDecimal summaExp;

    @JsonProperty("SUMMA_MAT")
    @NotNull(message = "SUMMA_MAT must not be null")
    private BigDecimal summaMat;

    @JsonProperty("SUMMA_OBO")
    @NotNull(message = "SUMMA_OBO must not be null")
    private BigDecimal summaObo;

    @JsonProperty("ITOG_PR")
    @NotNull(message = "ITOG_PR must not be null")
    private BigDecimal itogPr;

    @JsonProperty("SUMMA_PPH")
    @NotNull(message = "SUMMA_PPH must not be null")
    private BigDecimal summaPph;

    @JsonProperty("SUMMA_PZP")
    @NotNull(message = "SUMMA_PZP must not be null")
    private BigDecimal summaPzp;

    @JsonProperty("SUMMA_SSO")
    @NotNull(message = "SUMMA_SSO must not be null")
    private BigDecimal summaSso;

    @JsonProperty("SUMMA_KR")
    @NotNull(message = "SUMMA_KR must not be null")
    private BigDecimal summaKr;

    @JsonProperty("SUMMA_NDS")
    @NotNull(message = "SUMMA_NDS must not be null")
    private BigDecimal summaNds;

    @JsonProperty("ITOG_ALL")
    @NotNull(message = "ITOG_ALL must not be null")
    private BigDecimal itogAll;
}
