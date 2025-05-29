package ptzt.f1Hub.instraestructure.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ptzt.f1Hub.application.services.league.LeagueService;
import ptzt.f1Hub.domain.mappers.LeagueMapper;
import ptzt.f1Hub.domain.models.original.League;
import ptzt.f1Hub.instraestructure.dto.in.league.LeagueInDto;
import ptzt.f1Hub.instraestructure.dto.out.league.LeagueOutDto;

@RestController
@RequestMapping("/leagues")
@RequiredArgsConstructor
public class LeagueController {

    private final LeagueService leagueService;
    private final LeagueMapper leagueMapper;

    @PostMapping
    public ResponseEntity<LeagueOutDto> create(@Valid @RequestBody LeagueInDto leagueInDto){

        return ResponseEntity.ok(
                leagueMapper.toOutDto(
                        leagueService.create(leagueMapper.toEntity(leagueInDto))
                )
        );

    }

    @PutMapping("/{id}")
    public ResponseEntity<LeagueOutDto> update(@PathVariable Long id , @Valid @RequestBody LeagueInDto leagueInDto){

        League entity = leagueService.getById(id);
        leagueMapper.toUpdate(leagueInDto,entity);

        return ResponseEntity.ok(
                leagueMapper.toOutDto(
                        leagueService.update(entity)
                )
        );

    }
}
