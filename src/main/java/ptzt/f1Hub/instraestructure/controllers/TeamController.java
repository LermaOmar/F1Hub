package ptzt.f1Hub.instraestructure.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ptzt.f1Hub.application.services.team.TeamService;
import ptzt.f1Hub.domain.mappers.TeamMapper;
import ptzt.f1Hub.domain.models.Team;
import ptzt.f1Hub.instraestructure.dto.in.team.TeamInDto;
import ptzt.f1Hub.instraestructure.dto.out.shared.DefaultResponseDto;
import ptzt.f1Hub.instraestructure.dto.out.shared.PageOutDto;
import ptzt.f1Hub.instraestructure.dto.out.team.TeamOutDto;
import ptzt.f1Hub.instraestructure.dto.out.team.TeamOutLimitedDto;

@RestController
@RequestMapping("/teams")
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;
    private final TeamMapper teamMapper;

    @GetMapping
    public ResponseEntity<PageOutDto<TeamOutLimitedDto>> getAll(@PageableDefault Pageable pageable){

        return ResponseEntity.ok(
                new PageOutDto<>(teamService.getAll(pageable).map(teamMapper::toOutLimitedDto))
        ) ;

    }

    @GetMapping("/{id}")
    public ResponseEntity<TeamOutDto> getById(@PathVariable Long id){

        return ResponseEntity.ok(
                teamMapper.toOutDto(
                        teamService.getById(id)
                )
        ) ;

    }

    @PostMapping
    public ResponseEntity<TeamOutDto> create(@Valid @RequestBody TeamInDto teamInDto){

        return ResponseEntity.ok(
                teamMapper.toOutDto(
                        teamService.create(teamMapper.toEntity(teamInDto))
                )
        );

    }

    @PutMapping("/{id}")
    public ResponseEntity<TeamOutDto> update(@Valid @RequestBody TeamInDto teamInDto, @PathVariable Long id){

        Team team = teamService.getById(id);

        teamMapper.toUpdate(teamInDto,team);

        return ResponseEntity.ok(
                teamMapper.toOutDto(
                        teamService.update(team)
                )
        );

    }

    @PutMapping("/deactivate/{id}")
    public ResponseEntity<DefaultResponseDto> deactivate(@PathVariable Long id){

        teamService.deactivate(id);

        return ResponseEntity.ok(
                new DefaultResponseDto(200, "Team has been deactivated")
        );

    }

    @PutMapping("/activate/{id}")
    public ResponseEntity<DefaultResponseDto> activate(@PathVariable Long id){

        teamService.activate(id);

        return ResponseEntity.ok(
                new DefaultResponseDto(200, "Team has been activated")
        );

    }
}
