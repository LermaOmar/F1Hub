package ptzt.f1Hub.instraestructure.dto.in.account;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import ptzt.f1Hub.domain.enums.Roles;

import java.util.List;

@Getter
@Setter
public class AccountInFullDto {

    private String password;

    @NotBlank(message = "Define una email")
    @Email(message = "El email debe ser válido")
    private String email;

    @NotBlank(message = "Define un nombre de usuario")
    private String username;

    @NotNull(message = "Define si está activo o no")
    private boolean active;

    @NotNull(message = "Define los roles del usuario")
    List<Roles> roles;
}
