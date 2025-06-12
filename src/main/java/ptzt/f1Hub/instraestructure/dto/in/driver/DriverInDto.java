package ptzt.f1Hub.instraestructure.dto.in.driver;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DriverInDto {

    @NotBlank(message = "Specify the name of the driver")
    private String name;

    @NotNull(message = "Specify the price of the driver")
    private Long price;

    @NotBlank(message = "Specify the nationality of the driver")
    private String nationality;

    @NotNull(message = "Specify if the team is active")
    private Boolean active;

    private String base64image;
}
