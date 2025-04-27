package ptzt.f1Hub.instraestructure.dto.out.account;

import ptzt.f1Hub.domain.enums.Roles;

public record AccountOutDto(

        Long id,

        String password,

        String email,

        String username,

        boolean active,

        Roles rol) {
}
