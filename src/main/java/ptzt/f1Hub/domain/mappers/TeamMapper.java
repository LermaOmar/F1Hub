package ptzt.f1Hub.domain.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ptzt.f1Hub.domain.models.Team;
import ptzt.f1Hub.instraestructure.dto.in.team.TeamInDto;
import ptzt.f1Hub.instraestructure.dto.out.team.TeamOutDto;
import ptzt.f1Hub.instraestructure.dto.out.team.TeamOutLimitedDto;

@Mapper(componentModel = "spring")
public interface TeamMapper {

    @Mapping(target = "points", ignore = true)
    @Mapping(target = "previousPoints", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "lineUp", ignore = true)
    Team toEntity(TeamInDto teamInDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "points", ignore = true)
    @Mapping(target = "previousPoints", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "lineUp", ignore = true)
    void toUpdate(TeamInDto teamInDto, @MappingTarget Team team);

    TeamOutDto toOutDto(Team team);

    TeamOutLimitedDto toOutLimitedDto(Team team);
}
