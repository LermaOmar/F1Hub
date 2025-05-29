package ptzt.f1Hub.instraestructure.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ptzt.f1Hub.application.services.account.AccountService;
import ptzt.f1Hub.application.services.appUser.AppUserService;
import ptzt.f1Hub.domain.mappers.AccountMapper;
import ptzt.f1Hub.domain.mappers.AppUserMapper;
import ptzt.f1Hub.domain.models.original.Account;
import ptzt.f1Hub.domain.models.original.AppUser;
import ptzt.f1Hub.instraestructure.dto.in.account.AccountInFullDto;
import ptzt.f1Hub.instraestructure.dto.in.appUser.AppUserIdInDto;
import ptzt.f1Hub.instraestructure.dto.out.appUser.AppUserOutDto;
import ptzt.f1Hub.instraestructure.dto.out.shared.DefaultResponseDto;

@RestController
@RequestMapping("/appUsers")
@RequiredArgsConstructor
public class AppUserController {

    private final AppUserMapper appUserMapper;
    private final AccountMapper accountMapper;
    private final AppUserService appUserService;
    private final AccountService accountService;


    @PostMapping
    public ResponseEntity<AppUserOutDto> create(@Valid @RequestBody AccountInFullDto accountInFullDto){

        Account entity = accountMapper.toEntity(accountInFullDto);

        Account registeredAccount = accountService.create(entity);

        AppUser registeredUser = new AppUser();

        registeredUser.setAccount(registeredAccount);

        return ResponseEntity.ok(
                appUserMapper.toDto(appUserService.create(registeredUser))
        );

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<DefaultResponseDto> delete(@PathVariable Long id){

        appUserService.delete(id);

        return ResponseEntity.ok(
                new DefaultResponseDto(200, "Se ha eliminado correctamente el usuario")
        );

    }

    @PutMapping("/{id}")
    public ResponseEntity<AppUserOutDto> update(@Valid @RequestBody AccountInFullDto accountInDto, @PathVariable Long id){

        Account entity = accountService.getById(id);

        accountMapper.toUpdate(accountInDto, entity);

        if (accountInDto.getPassword() == null ||accountInDto.getPassword().isBlank())
            entity.setPassword(null);


        return ResponseEntity.ok(
                appUserMapper.toDto(appUserService.getByAccount(accountService.update(entity)))
        );

    }

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
