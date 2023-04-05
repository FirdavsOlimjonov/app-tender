package uz.mc.apptender.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Setter
@Getter
@Builder
public class SmetaItogDTO {

    @JsonProperty("ZATR_TRUD")
    private Double zatrTrud;

    @JsonProperty("SUMMA_ZP")
    private BigDecimal summaZp;

    @JsonProperty("SUMMA_EXP")
    private BigDecimal summaExp;

    @JsonProperty("SUMMA_MAT")
    private BigDecimal summaMat;

    @JsonProperty("SUMMA_OBO")
    private BigDecimal summaObo;

    @JsonProperty("ITOG_PR")
    private BigDecimal itogPr;

    @JsonProperty("SUMMA_PPH")
    private BigDecimal summaPph;

    @JsonProperty("SUMMA_PZP")
    private BigDecimal summaPzp;

    @JsonProperty("SUMMA_SSO")
    private BigDecimal summaSso;

    @JsonProperty("SUMMA_KR")
    private BigDecimal summaKr;

    @JsonProperty("SUMMA_NDS")
    private BigDecimal summaNds;

    @JsonProperty("ITOG_ALL")
    private BigDecimal itogAll;
}
