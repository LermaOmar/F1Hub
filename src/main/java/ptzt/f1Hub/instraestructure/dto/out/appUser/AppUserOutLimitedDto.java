package ptzt.f1Hub.instraestructure.dto.out.appUser;

import ptzt.f1Hub.instraestructure.dto.out.account.AccountLimitedOutDto;


public record AppUserOutLimitedDto(

         Long id,

         AccountLimitedOutDto account
) {}
