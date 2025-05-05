package ptzt.f1Hub.domain.mappers;

import org.mapstruct.Mapper;
import ptzt.f1Hub.application.services.account.AccountService;
import ptzt.f1Hub.application.services.appUser.AppUserService;
import ptzt.f1Hub.domain.models.AppUser;
import ptzt.f1Hub.instraestructure.dto.in.appUser.AppUserIdInDto;
import ptzt.f1Hub.instraestructure.dto.out.appUser.AppUserOutDto;
import ptzt.f1Hub.instraestructure.dto.out.appUser.AppUserOutLimitedDto;

@Mapper(componentModel = "spring", uses = {AccountService.class, AppUserService.class})
public interface AppUserMapper {


    AppUser toEntity(AppUserIdInDto appUserIdInDto);

    AppUserOutDto toDto(AppUser appUser);

    AppUserOutLimitedDto toLimitedDto(AppUser appUser);

}
