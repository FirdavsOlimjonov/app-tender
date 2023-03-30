package uz.mc.apptender.payload;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Error {
    List<String> inn;
    List<String> lot_id;
    int code;

    @Override
    public String toString() {
        return "inn: " + inn + ", lot_id=" + lot_id;
    }
}

