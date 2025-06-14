package ptzt.f1Hub.domain.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ptzt.f1Hub.domain.models.original.Account;
import ptzt.f1Hub.instraestructure.dto.in.account.AccountInDto;
import ptzt.f1Hub.instraestructure.dto.in.account.AccountInFullDto;
import ptzt.f1Hub.instraestructure.dto.out.account.AccountLimitedOutDto;
import ptzt.f1Hub.instraestructure.dto.out.account.AccountOutDto;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "appUser", ignore = true)
    Account toEntity(AccountInDto accountInDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "appUser", ignore = true)
    Account toEntity(AccountInFullDto accountInDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "appUser", ignore = true)
    @Mapping(target = "password", ignore = true)
    void toUpdate(AccountInFullDto accountInDto, @MappingTarget Account account);

    AccountOutDto toDto(Account account);

    AccountLimitedOutDto toLimitedDto(Account account);

}
