package uz.mc.apptender.modules;

import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Where;
import uz.mc.apptender.modules.enums.RoleEnum;
import uz.mc.apptender.modules.templates.AbsTimestampEntity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@DynamicUpdate
@Where(clause = "deleted = false")
@ToString
public class Stroy extends AbsTimestampEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String strName;

    @Column(nullable = false)
    private Integer tenderId;

    @Column(nullable = false)
    private long lotId;

    @Column(precision=20, scale=5)
    private BigDecimal sum;

    private boolean deleted = false;

    @OneToMany(mappedBy = "stroy",fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<Object> obArray;

    @OneToMany(mappedBy = "stroy",fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<SvodResurs> svod_resurs;

    public Stroy(String strName, Integer tenderId,  long lotId) {
        this.strName = strName;
        this.tenderId = tenderId;
        this.lotId = lotId;
    }

    public Stroy(Integer tenderId, long lotId) {
        this.tenderId = tenderId;
        this.lotId = lotId;
    }
}
