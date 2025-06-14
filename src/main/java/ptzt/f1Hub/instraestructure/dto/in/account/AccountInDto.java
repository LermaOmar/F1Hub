package ptzt.f1Hub.instraestructure.dto.in.account;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountInDto {

    @NotBlank(message = "Define una contraseña")
    private String password;

    @NotBlank(message = "Define una email")
    @Email(message = "El email debe ser válido")
    private String email;

    @NotBlank(message = "Define un nombre de usuario")
    private String username;
}
