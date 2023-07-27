package uz.mc.apptender.modules;

import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Where;
import uz.mc.apptender.modules.templates.AbsLongEntity;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@DynamicUpdate
@ToString
@Where(clause = "deleted = false")
public class SvodResurs extends AbsLongEntity {
    private Integer num;

    private Integer kodv;

    private Integer tip;

    private String kodr;

    private String kodm;

    private String kodiName;

    private String name;

    private Double kol;

    private BigDecimal price;

    private BigDecimal summa;

    @ManyToOne(optional = false)
    private Stroy stroy;
}
