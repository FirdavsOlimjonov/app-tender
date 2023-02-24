//package uz.mc.apptender.modules;
//
//import lombok.AllArgsConstructor;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//import uz.mc.apptender.modules.enums.PermissionEnum;
//import uz.mc.apptender.modules.templates.AbsIntegerEntity;
//
//import javax.persistence.*;
//import java.util.List;
//import java.util.Set;
//
//@Entity
//@Setter
//@Getter
//@NoArgsConstructor
//@AllArgsConstructor
//public class Role extends AbsIntegerEntity {
//    @Column(nullable = false, unique = true)
//    private String name;
//    @Column(nullable = false)
//    private String description;
//    @ElementCollection
//    @Enumerated(value = EnumType.STRING)
//    private Set<PermissionEnum> permissions;
//    @OneToMany(mappedBy = "role",fetch = FetchType.LAZY)
//    private List<User> users;
//}
