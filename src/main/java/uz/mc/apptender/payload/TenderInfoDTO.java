package uz.mc.apptender.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import uz.mc.apptender.utils.MessageConstants;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@Builder
public class TenderInfoDTO {
    private Integer tenderId;
    private Integer id;

    private Integer catId;

    private Integer userId;

    private Integer type;

    private String kodSnk;

    private String name;

    private String edIsm;

    private Double norma;

    private BigDecimal rashod;

    private BigDecimal price;

    private BigDecimal summa;
}
