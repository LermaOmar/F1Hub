package ptzt.f1Hub.instraestructure.dto.in.account;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountLoginDto {

    @NotEmpty(message = "Specify the email")
    private String email;

    @NotEmpty(message = "Specify the password")
    private String password;

}
