package ptzt.f1Hub.instraestructure.dto.in.account;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import ptzt.f1Hub.domain.enums.Roles;
import ptzt.f1Hub.domain.models.AppUser;

@Getter
@Setter
public class AccountInDto {

    @NotBlank(message = "Define una contraseña")
    private String password;

    @NotBlank(message = "Define una contraseña")
    @Email(message = "El email debe ser válido")
    private String email;

    @NotBlank(message = "Define un nombre de usuario")
    private String username;
}
