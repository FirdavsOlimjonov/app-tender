package uz.mc.apptender.modules;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Where;
import uz.mc.apptender.modules.enums.RoleEnum;
import uz.mc.apptender.modules.templates.AbsTimestampEntity;

import javax.persistence.*;
import java.util.List;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@DynamicUpdate
@Where(clause = "deleted = false")
public class Stroy extends AbsTimestampEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String strName;

    @Column(nullable = false)
    private Integer tenderId;

    @Column(nullable = false)
    private long userId;

    @Column(nullable = false)
    private long lotId;

    @Enumerated(EnumType.STRING)
    private RoleEnum role;

    private boolean deleted = false;
    @OneToMany(mappedBy = "stroy",fetch = FetchType.LAZY)
    private List<Object> obArray;

    public Stroy(String strName, Integer tenderId, long userid, long lotId,RoleEnum role) {
        this.strName = strName;
        this.tenderId = tenderId;
        this.userId = userid;
        this.lotId = lotId;
        this.role = role;
    }

    public Stroy(Integer tenderId, long userId, long lotId, RoleEnum role) {
        this.tenderId = tenderId;
        this.userId = userId;
        this.lotId = lotId;
        this.role = role;
    }
}
