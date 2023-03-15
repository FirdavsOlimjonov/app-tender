package uz.mc.apptender.modules;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import uz.mc.apptender.modules.enums.RoleEnum;
import uz.mc.apptender.modules.templates.AbsIntegerEntity;

import javax.persistence.*;
import java.util.List;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@DynamicUpdate
@DynamicInsert
public class Smeta{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false)
    private String smName;
    @Column(nullable = false)
    private String smNum;
    @Enumerated(EnumType.STRING)
    private RoleEnum role;
    @Column(nullable = false)
    private long userId;
    @ManyToOne(optional = false)
    private Object object;
    @OneToMany(mappedBy = "smeta",fetch = FetchType.LAZY)
    private List<TenderCustomer> smeta;

    public Smeta(String smName, String smNum, RoleEnum role, long userId, Object object) {
        this.smName = smName;
        this.smNum = smNum;
        this.role = role;
        this.userId = userId;
        this.object = object;
    }
}
