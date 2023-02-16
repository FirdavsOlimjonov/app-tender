package uz.mc.apptender.modules;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.mc.apptender.modules.templates.AbsIntegerEntity;

import javax.persistence.*;
import java.util.List;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Stroy extends AbsIntegerEntity {
    @Column(nullable = false, unique = true)
    private String strName;
    @Column(nullable = false,updatable = false)
    private Integer tenderId;
    @OneToMany(mappedBy = "stroy",fetch = FetchType.LAZY)
    private List<Object> obArray;

    public Stroy(String strName, Integer tenderId) {
        this.strName = strName;
        this.tenderId = tenderId;
    }
}
