
package ptzt.f1Hub.instraestructure.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import ptzt.f1Hub.application.services.account.AccountService;
import ptzt.f1Hub.application.services.appUser.AppUserService;
import ptzt.f1Hub.application.services.league.LeagueService;
import ptzt.f1Hub.application.services.lineUp.LineUpService;
import ptzt.f1Hub.domain.mappers.LineUpMapper;
import ptzt.f1Hub.domain.models.original.LineUp;
import ptzt.f1Hub.instraestructure.dto.out.league.LeagueOutLimitedDto;
import ptzt.f1Hub.instraestructure.dto.out.lineUp.LineUpOutDto;
import ptzt.f1Hub.instraestructure.dto.out.shared.PageOutDto;

import java.util.Comparator;


@RestController
@RequestMapping("/lineUps")
@RequiredArgsConstructor
public class LineUpController {

    private final LineUpService lineUpService;
    private final AccountService accountService;
    private final AppUserService appUserService;
    private final LeagueService leagueService;
    private final LineUpMapper lineUpMapper;

    @GetMapping("/{id}/league/mine")
    public ResponseEntity<LineUpOutDto> getByUserAndLeague(@PathVariable Long id, Authentication authentication){

        return ResponseEntity.ok(
                lineUpMapper.toOutDto(

                    lineUpService.getByAppUserAndLeague(
                            appUserService.getByAccount(accountService.getByEmail(authentication.getName())), leagueService.getById(id)
                    )
                )

        );

    }

    @GetMapping("/{id}/league/ranking")
    public ResponseEntity<PageOutDto<LineUpOutDto>> getRankingByLeague(@PathVariable Long id,
                                                                       @PageableDefault(sort = "totalPoints", direction = Sort.Direction.DESC) Pageable pageable){

        return ResponseEntity.ok(
                new PageOutDto<>(
                        lineUpService.getAllByLeague(pageable, leagueService.getById(id)).map(lineUpMapper::toOutDto)
                )
        );

    }

    @GetMapping("/{id}/league")
    public ResponseEntity<PageOutDto<LineUpOutDto>> getAllByLeague(@PageableDefault Pageable pageable, @PathVariable Long id){

        return ResponseEntity.ok(
                new PageOutDto<>(
                        lineUpService.getAllByLeague(pageable, leagueService.getById(id))
                                .map(lineUpMapper::toOutDto)
                )
        );

    }


}
