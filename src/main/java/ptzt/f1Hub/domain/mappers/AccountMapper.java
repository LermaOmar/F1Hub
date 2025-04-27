package ptzt.f1Hub.domain.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ptzt.f1Hub.domain.enums.Roles;
import ptzt.f1Hub.domain.models.Account;
import ptzt.f1Hub.instraestructure.dto.in.account.AccountInDto;
import ptzt.f1Hub.instraestructure.dto.out.account.AccountLimitedOutDto;
import ptzt.f1Hub.instraestructure.dto.out.account.AccountOutDto;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "rol", ignore = true)
    @Mapping(target = "appUser", ignore = true)
    Account toEntity(AccountInDto accountInDto);

    void toUpdate(AccountInDto accountInDto, @MappingTarget Account account);

    AccountOutDto toDto(Account account);

    AccountLimitedOutDto toLimitedDto(Account account);

}
