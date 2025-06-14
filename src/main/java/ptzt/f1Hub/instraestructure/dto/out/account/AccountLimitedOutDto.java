package ptzt.f1Hub.instraestructure.dto.out.account;

import ptzt.f1Hub.domain.enums.Roles;

import java.util.List;

public record AccountLimitedOutDto(

        Long id,

        String email,

        String username,

        List<Roles> roles
) {}
