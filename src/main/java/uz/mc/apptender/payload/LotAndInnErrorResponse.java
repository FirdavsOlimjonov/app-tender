package uz.mc.apptender.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LotAndInnErrorResponse {
    List<String> inn;
    List<String> lot_id;

    @Override
    public String toString() {
        return  "inn: " + inn +
                ", lot_id: " + lot_id;
    }
}
