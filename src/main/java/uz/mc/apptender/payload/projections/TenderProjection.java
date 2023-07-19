package uz.mc.apptender.payload.projections;

import java.math.BigDecimal;

public interface TenderProjection {
    String getKod_snk();
    Long getId();
    String getName();
    String getEd_ism();
    Double getNorma();
    BigDecimal getPrice();
    BigDecimal getSumma();
}