package uz.mc.apptender.modules;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Where;
import uz.mc.apptender.modules.enums.RoleEnum;
import uz.mc.apptender.modules.templates.AbsIntegerEntity;
import uz.mc.apptender.modules.templates.AbsTimestampEntity;

import javax.persistence.*;
import java.util.List;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@DynamicUpdate
@DynamicInsert
@Where(clause = "deleted = false")
public class Smeta extends AbsTimestampEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String smName;

    @Column(nullable = false)
    private String smNum;

    @Column(nullable = false)
    private long userId;

    @OneToOne(mappedBy = "smeta", cascade = CascadeType.ALL)
    private SmetaItog smetaItog;

    @ManyToOne(optional = false)
    private Object object;

    private boolean deleted = false;

    @OneToMany(mappedBy = "smeta", fetch = FetchType.LAZY)
    private List<TenderCustomer> smeta_customer;

    @OneToMany(mappedBy = "smeta", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<TenderOfferor> smeta_offeror;

    public Smeta(String smName, String smNum, long userId, Object object) {
        this.smName = smName;
        this.smNum = smNum;
        this.userId = userId;
        this.object = object;
    }
}
