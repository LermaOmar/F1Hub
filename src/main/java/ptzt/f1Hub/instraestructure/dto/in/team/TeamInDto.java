package ptzt.f1Hub.instraestructure.dto.in.team;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TeamInDto {

    @NotBlank(message = "Specify the name of the team")
    private String name;

    @NotNull(message = "Specify the price of the team")
    private Long price;

    @NotBlank(message = "Specify the nationality of the team")
    private String nationality;

    @NotNull(message = "Specify if the team is active")
    private Boolean active;

}
