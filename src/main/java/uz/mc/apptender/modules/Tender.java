package uz.mc.apptender.modules;

import lombok.*;
import uz.mc.apptender.modules.enums.PermissionEnum;
import uz.mc.apptender.modules.templates.AbsIntegerEntity;
import uz.mc.apptender.modules.templates.AbsTimestampEntity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tender extends AbsTimestampEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer smId;

    @Column(nullable = false)
    private Integer id;

    @Column(nullable = false)
    private Integer catId;

    @Column(nullable = false)
    private Integer userId;

    @Column(nullable = false)
    private Integer type;

//    @Column(nullable = false)
    private String kodSnk;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String edIsm;

    @Column(nullable = false)
    private Double norma;

    @Column(nullable = false,precision=20, scale=10)
    private BigDecimal rashod;

    @Column(nullable = false,precision=20, scale=10)
    private BigDecimal price;

    @Column(nullable = false,precision=20, scale=10)
    private BigDecimal summa;

    @ManyToOne(optional = false)
    private Smeta smeta;

}
