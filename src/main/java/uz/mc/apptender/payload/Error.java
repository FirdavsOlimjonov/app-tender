package uz.mc.apptender.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.concurrent.Executor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Error extends RuntimeException {
    List<LotAndInnErrorResponse> error;
    int code;

    @Override
    public String toString() {
        return "error: "+error+
                ", code: "+code;
    }
}

