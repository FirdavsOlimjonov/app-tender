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
    private Integer tender_id;
    private Integer id;

    private Integer cat_id;

    private Integer user_id;

    private Integer type;

    private String kod_snk;

    private String name;

    private String ed_ism;

    private Double norma;

    private BigDecimal rashod;

    private BigDecimal price;

    private BigDecimal summa;
}
