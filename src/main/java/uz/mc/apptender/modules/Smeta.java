package uz.mc.apptender.modules;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.mc.apptender.modules.enums.PermissionEnum;
import uz.mc.apptender.modules.templates.AbsIntegerEntity;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Smeta extends AbsIntegerEntity {
    @Column(nullable = false)
    private String smName;
    @Column(nullable = false)
    private String smNum;
    @ManyToOne(optional = false)
    private Object object;
    @OneToMany(mappedBy = "smeta",fetch = FetchType.LAZY)
    private List<Tender> smeta;

    public Smeta(String smName, String smNum, Object object) {
        this.smName = smName;
        this.smNum = smNum;
        this.object = object;
    }
}
