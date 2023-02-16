package uz.mc.apptender.modules;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.mc.apptender.modules.enums.PermissionEnum;
import uz.mc.apptender.modules.templates.AbsIntegerEntity;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Object extends AbsIntegerEntity {
    @Column(nullable = false)
    private String obName;
    @Column(nullable = false)
    private String obNum;
    @ManyToOne(optional = false)
    private Stroy stroy;
    @OneToMany(mappedBy = "object",fetch = FetchType.LAZY)
    private List<Smeta> smArray;

    public Object(String obName, String obNum, Stroy stroy) {
        this.obName = obName;
        this.obNum = obNum;
        this.stroy = stroy;
    }
}
