package ptzt.f1Hub.instraestructure.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ptzt.f1Hub.application.services.account.AccountService;
import ptzt.f1Hub.application.services.appUser.AppUserService;
import ptzt.f1Hub.application.services.league.LeagueService;
import ptzt.f1Hub.domain.mappers.LeagueMapper;
import ptzt.f1Hub.domain.models.original.League;
import ptzt.f1Hub.instraestructure.dto.in.league.LeagueInDto;
import ptzt.f1Hub.instraestructure.dto.out.league.LeagueOutDto;
import ptzt.f1Hub.instraestructure.dto.out.league.LeagueOutLimitedDto;
import ptzt.f1Hub.instraestructure.dto.out.shared.PageOutDto;

@RestController
@RequestMapping("/leagues")
@RequiredArgsConstructor
public class LeagueController {

    private final LeagueService leagueService;
    private final AppUserService appUserService;
    private final AccountService accountService;
    private final LeagueMapper leagueMapper;

    @PostMapping
    public ResponseEntity<LeagueOutDto> create(@Valid @RequestBody LeagueInDto leagueInDto, Authentication authentication){

        return ResponseEntity.ok(
            leagueMapper.toOutDto(
                leagueService.create(
                    leagueMapper.toEntity(leagueInDto),appUserService.getByAccount(
                        accountService.getByEmail(authentication.getName())

                    )
                )
            )
        );

    }

    @GetMapping
    public ResponseEntity<PageOutDto<LeagueOutLimitedDto>> getAllByUser(@PageableDefault Pageable pageable,
                                                                        Authentication authentication){

        return ResponseEntity.ok(
                    new PageOutDto<>(
                            leagueService.getAllByUser(pageable, appUserService.getByAccount(
                                    accountService.getByEmail(authentication.getName()))
                            ).map(leagueMapper::toOutLimitedDto)
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
