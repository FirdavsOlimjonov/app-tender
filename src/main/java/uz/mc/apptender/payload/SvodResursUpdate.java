package uz.mc.apptender.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SvodResursUpdate {
    @NotNull(message = "Lot_Id must be not null")
    Long inn;

    @NotNull(message = "Inn must be not null")
    Long lot_id;

    @NotNull(message = "svod_resurs must be not null")
    List<SvodResursDAO> svod_resurs;
}
