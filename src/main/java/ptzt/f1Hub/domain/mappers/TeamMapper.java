package ptzt.f1Hub.domain.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ptzt.f1Hub.domain.models.original.Team;
import ptzt.f1Hub.instraestructure.dto.in.team.TeamInDto;
import ptzt.f1Hub.instraestructure.dto.out.auctionableEntities.TeamOutDto;
import ptzt.f1Hub.instraestructure.dto.out.auctionableEntities.TeamOutLimitedDto;

@Mapper(componentModel = "spring")
public interface TeamMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "points", ignore = true)
    @Mapping(target = "previousPoints", ignore = true)
    @Mapping(target = "lineUps", ignore = true)
    @Mapping(target = "marketItems", ignore = true)
    Team toEntity(TeamInDto teamInDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "points", ignore = true)
    @Mapping(target = "previousPoints", ignore = true)
    @Mapping(target = "lineUps", ignore = true)
    @Mapping(target = "marketItems", ignore = true)
    void toUpdate(TeamInDto teamInDto, @MappingTarget Team team);

    TeamOutDto toOutDto(Team team);

    @Mapping(target = "type", expression = "java(\"Team\")")
    TeamOutLimitedDto toOutLimitedDto(Team team);
}
