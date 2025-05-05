package ptzt.f1Hub.instraestructure.dto.out.league;


import ptzt.f1Hub.instraestructure.dto.out.lineUp.LineUpOutDto;

import java.util.List;

public record LeagueOutDto (

        Long id,

        String name,

        List<LineUpOutDto> lineUps
){
}
