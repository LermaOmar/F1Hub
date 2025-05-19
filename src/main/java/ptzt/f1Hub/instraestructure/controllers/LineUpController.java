
package ptzt.f1Hub.instraestructure.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ptzt.f1Hub.application.services.appUser.AppUserService;
import ptzt.f1Hub.application.services.league.LeagueService;
import ptzt.f1Hub.application.services.lineUp.LineUpService;
import ptzt.f1Hub.domain.mappers.LineUpMapper;
import ptzt.f1Hub.instraestructure.dto.out.lineUp.LineUpOutDto;
import ptzt.f1Hub.instraestructure.dto.out.shared.PageOutDto;


@RestController
@RequestMapping("/lineUps")
@RequiredArgsConstructor
public class LineUpController {

    private final LineUpService lineUpService;
    private final AppUserService appUserService;
    private final LeagueService leagueService;
    private final LineUpMapper lineUpMapper;

    @GetMapping("/{id}/user")
    public ResponseEntity<PageOutDto<LineUpOutDto>> getAllByUser(@PageableDefault Pageable pageable, @PathVariable Long id){

        return ResponseEntity.ok(
                new PageOutDto<>(
                        lineUpService.getAllByUser(pageable,appUserService.getById(id)).map(lineUpMapper::toOutDto)
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
