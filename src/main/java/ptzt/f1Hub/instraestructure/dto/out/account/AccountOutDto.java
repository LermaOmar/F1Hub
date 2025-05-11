package ptzt.f1Hub.instraestructure.dto.out.account;

import ptzt.f1Hub.domain.enums.Roles;

import java.util.List;

public record AccountOutDto(

        Long id,

        String password,

        String email,

        String username,

        boolean active,

        List<Roles> roles) {
}
