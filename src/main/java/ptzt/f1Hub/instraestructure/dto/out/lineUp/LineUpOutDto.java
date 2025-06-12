package ptzt.f1Hub.instraestructure.dto.out.lineUp;

import ptzt.f1Hub.instraestructure.dto.out.appUser.AppUserOutLimitedDto;
import ptzt.f1Hub.instraestructure.dto.out.auctionableEntities.DriverOutLimitedDto;
import ptzt.f1Hub.instraestructure.dto.out.auctionableEntities.TeamOutLimitedDto;

import java.util.List;

public record LineUpOutDto(

        Long id,

        AppUserOutLimitedDto appUser,

        List<DriverOutLimitedDto> drivers,

        TeamOutLimitedDto team
) {
}
