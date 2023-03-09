package uz.mc.apptender.modules;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.mc.apptender.modules.enums.RoleEnum;
import uz.mc.apptender.modules.templates.AbsIntegerEntity;

import javax.persistence.*;
import java.util.List;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Stroy extends AbsIntegerEntity {
    @Column(nullable = false)
    private String strName;

    @Column(nullable = false)
    private Integer tenderId;

    @Column(nullable = false)
    private long userid;

    private RoleEnum role;

    @OneToMany(mappedBy = "stroy",fetch = FetchType.LAZY)
    private List<Object> obArray;

    public Stroy(String strName, Integer tenderId, long userid) {
        this.strName = strName;
        this.tenderId = tenderId;
        this.userid = userid;
    }
}
