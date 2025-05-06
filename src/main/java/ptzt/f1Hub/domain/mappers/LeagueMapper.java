package ptzt.f1Hub.domain.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ptzt.f1Hub.application.services.league.LeagueService;
import ptzt.f1Hub.domain.models.League;
import ptzt.f1Hub.instraestructure.dto.in.league.LeagueInDto;
import ptzt.f1Hub.instraestructure.dto.in.league.LeagueInIdDto;
import ptzt.f1Hub.instraestructure.dto.out.league.LeagueOutDto;

@Mapper(componentModel = "spring", uses = LeagueService.class)
public interface LeagueMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "lineUps", ignore = true)
    @Mapping(target = "offers", ignore = true)
    @Mapping(target = "budgets", ignore = true)
    League toEntity(LeagueInDto leagueInDto);

    League toEntity(LeagueInIdDto leagueInIdDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "lineUps", ignore = true)
    @Mapping(target = "offers", ignore = true)
    @Mapping(target = "budgets", ignore = true)
    League toUpdate(LeagueInDto leagueInDto, @MappingTarget League league);

    LeagueOutDto toOutDto(League league);
}
