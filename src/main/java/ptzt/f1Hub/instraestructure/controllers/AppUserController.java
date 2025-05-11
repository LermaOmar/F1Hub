package ptzt.f1Hub.instraestructure.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ptzt.f1Hub.application.services.appUser.AppUserService;
import ptzt.f1Hub.domain.mappers.AppUserMapper;
import ptzt.f1Hub.domain.models.AppUser;
import ptzt.f1Hub.instraestructure.dto.in.appUser.AppUserIdInDto;
import ptzt.f1Hub.instraestructure.dto.out.shared.DefaultResponseDto;

@RestController
@RequestMapping("/appUsers")
@RequiredArgsConstructor
public class AppUserController {

    private final AppUserService appUserService;

    @PutMapping("/joinLeague/{leagueId}")
    public ResponseEntity<DefaultResponseDto> joinLeague(@PathVariable Long leagueId, @Valid @RequestBody AppUserIdInDto appUserIdInDtop){

        appUserService.joinLeague(appUserIdInDtoToEntity(appUserIdInDtop),leagueId);

        return ResponseEntity.ok(
                new DefaultResponseDto(200,"User has joined the league")
        );

    }

    @PutMapping("/leaveLeague/{leagueId}")
    public ResponseEntity<DefaultResponseDto> leaveLeague(@PathVariable Long leagueId, @Valid @RequestBody AppUserIdInDto appUserIdInDtop){

        appUserService.leaveLeague(appUserIdInDtoToEntity(appUserIdInDtop),leagueId);

        return ResponseEntity.ok(
                new DefaultResponseDto(200,"User has left the league")
        );

    }

    private AppUser appUserIdInDtoToEntity(AppUserIdInDto appUserIdInDto){

        return appUserService.getById(appUserIdInDto.getId());

    }

}
