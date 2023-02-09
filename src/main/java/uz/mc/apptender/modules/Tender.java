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
    @Column(updatable = false,unique = true)
    private Integer id;

    @Column(nullable = false,updatable = false)
    private Integer tenderId;

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

    @Column(nullable = false,precision=15, scale=7)
    private BigDecimal rashod;

    @Column(nullable = false,precision=15, scale=7)
    private BigDecimal price;

    @Column(nullable = false,precision=15, scale=7)
    private BigDecimal summa;

}
