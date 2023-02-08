package uz.mc.apptender.modules.enums;


import org.springframework.security.core.GrantedAuthority;

public enum PermissionEnum implements GrantedAuthority {
    ADD_PROJECT,
    EDIT_PROJECT,
    UPLOAD_FILES,
    UPLOAD_IMAGES,
    GET_USERS,
    DELETE_USER,
    GET_PROJECTS;

    @Override
    public String getAuthority() {
        return this.name();
    }
}
