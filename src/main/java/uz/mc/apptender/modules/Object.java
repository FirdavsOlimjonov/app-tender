package uz.mc.apptender.modules;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Where;
import uz.mc.apptender.modules.enums.PermissionEnum;
import uz.mc.apptender.modules.enums.RoleEnum;
import uz.mc.apptender.modules.templates.AbsIntegerEntity;
import uz.mc.apptender.modules.templates.AbsTimestampEntity;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@DynamicUpdate
@DynamicInsert
@Where(clause = "deleted = false")
public class Object extends AbsTimestampEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String obName;

    @Column(nullable = false)
    private String obNum;

    @Column(nullable = false)
    private long userId;

    @ManyToOne(optional = false)
    private Stroy stroy;

    private boolean deleted = false;

    @OneToMany(mappedBy = "object",fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Smeta> smArray;

    public Object(String obName, String obNum, long userId, Stroy stroy) {
        this.obName = obName;
        this.obNum = obNum;
        this.userId = userId;
        this.stroy = stroy;
        deleted = false;
    }
}
