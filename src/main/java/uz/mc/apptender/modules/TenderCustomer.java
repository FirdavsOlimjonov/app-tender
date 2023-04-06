package uz.mc.apptender.modules;

import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Where;
import uz.mc.apptender.modules.enums.RoleEnum;
import uz.mc.apptender.modules.templates.AbsTimestampEntity;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@DynamicInsert
@DynamicUpdate
@Where(clause = "deleted = false")
public class TenderCustomer extends AbsTimestampEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long smId;

    @Column(nullable = false)
    private Integer num;

    @Column(nullable = false)
    private Integer rowType;

    @Column(nullable = false)
    private Integer opred;

    @Column(nullable = false)
    private Long userId;

//    @Column(nullable = false)
    private String kodSnk;

    @Column(nullable = false, columnDefinition = "text")
    private String name;

    @Column(nullable = false)
    private String edIsm;

    @Column(nullable = false)
    private Double norma;

    @Column(nullable = false)
    private Double rashod;

    @Column(nullable = false,precision=20, scale=5)
    private BigDecimal price;

    @Column(nullable = false,precision=20, scale=5)
    private BigDecimal summa;

    @ManyToOne
    private Smeta smeta;

    private boolean deleted = false;

    private Long parentId;
}
