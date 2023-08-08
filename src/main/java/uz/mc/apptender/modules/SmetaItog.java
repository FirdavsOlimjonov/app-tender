package uz.mc.apptender.modules;

import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Where;
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
public class SmetaItog extends AbsTimestampEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double zatrTrud;

    @Column(precision=20, scale=5)
    private BigDecimal summaZp;

    @Column(precision=20, scale=5)
    private BigDecimal summaExp;

    @Column(precision=20, scale=5)
    private BigDecimal summaMat;

    @Column(precision=20, scale=5)
    private BigDecimal summaObo;

    @Column(precision=20, scale=5)
    private BigDecimal itogPr;

    @Column(precision=20, scale=5)
    private BigDecimal summaPph;

    @Column(precision=20, scale=5)
    private BigDecimal summaPzp;

    @Column(precision=20, scale=5)
    private BigDecimal summaSso;

    @Column(precision=20, scale=5)
    private BigDecimal summaKr;

    @Column(precision=20, scale=5)
    private BigDecimal summaNds;

    @Column(precision=20, scale=5)
    private BigDecimal itogAll;

    @OneToOne(optional = false)
    private Smeta smeta;

    @ManyToOne(optional = false)
    private Stroy stroy;

    private boolean deleted = false;

}
