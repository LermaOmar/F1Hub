package ptzt.f1Hub.instraestructure.dto.out.appUser;

import ptzt.f1Hub.domain.models.original.LineUp;
import ptzt.f1Hub.instraestructure.dto.out.account.AccountLimitedOutDto;

import java.util.List;

public record AppUserOutDto(

         Long id,

         AccountLimitedOutDto account,

        List<LineUp> lineUps
) {}
