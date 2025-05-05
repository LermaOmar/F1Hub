package ptzt.f1Hub.instraestructure.dto.in.appUser;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AppUserIdInDto {

    @NotNull(message = "Specify the id of the user you want to register to a league")
    private Long id;

}
