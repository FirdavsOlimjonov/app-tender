package uz.mc.apptender.payload.projections;

import javax.persistence.Column;

public interface SmetaGetProjection {
    Long getId();
    String getSmNum();
    String getSmName();
    Long getUserId();

}
