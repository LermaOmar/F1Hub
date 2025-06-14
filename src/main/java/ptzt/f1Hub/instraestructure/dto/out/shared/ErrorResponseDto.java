package ptzt.f1Hub.instraestructure.dto.out.shared;

import java.time.LocalDateTime;

public record ErrorResponseDto(

        int status,

        LocalDateTime timeStamp,

        String error
) {
}
