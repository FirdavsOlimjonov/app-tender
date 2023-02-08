package uz.mc.apptender.modules;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import uz.mc.apptender.modules.templates.AbsUUIDEntity;

import javax.persistence.*;
import java.util.Collection;

@Entity
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
@DynamicInsert
@DynamicUpdate
@SQLDelete(sql = "UPDATE users SET deleted = true")
@Where(clause = "deleted = false")
public class User extends AbsUUIDEntity implements UserDetails {
    @Column(nullable = false)
    private String username;
    @Column(nullable = false)
    private String password;
    private boolean enabled = true;
    @ManyToOne(fetch = FetchType.LAZY)
    private Role role;

    private boolean deleted;

    public User(String username, String password) {
        this.password = password;
        this.username = username;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return role.getPermissions();
    }

    @Override
    public String getPassword() {
        return password;
    }

    public Role getRole() {
        return role;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return enabled;
    }

    @Override
    public boolean isAccountNonLocked() {
        return enabled;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return enabled;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

}
