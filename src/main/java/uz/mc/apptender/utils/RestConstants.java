package uz.mc.apptender.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

public interface RestConstants {
    ObjectMapper objectMapper = new ObjectMapper();
    String BASE_PATH = "/api/v1/";

    String[] OPEN_PAGES = {
            "/swagger-ui/**","/*","/auth/login"
    };
}
