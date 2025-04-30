package ptzt.f1Hub.instraestructure.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ptzt.f1Hub.application.services.account.AccountService;
import ptzt.f1Hub.application.services.appUser.AppUserService;
import ptzt.f1Hub.domain.enums.Roles;
import ptzt.f1Hub.domain.mappers.AccountMapper;
import ptzt.f1Hub.domain.mappers.AppUserMapper;
import ptzt.f1Hub.domain.models.Account;
import ptzt.f1Hub.domain.models.AppUser;
import ptzt.f1Hub.instraestructure.dto.in.account.AccountInDto;
import ptzt.f1Hub.instraestructure.dto.out.appUser.AppUserOutDto;

import java.util.ArrayList;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AccountService accountService;
    private final AppUserService appUserService;

    private final AccountMapper accountMapper;
    private final AppUserMapper appUserMapper;

    @PostMapping("/register")
    public ResponseEntity<AppUserOutDto> login(@Valid @RequestBody AccountInDto accountInDto){

        Account entity = accountMapper.toEntity(accountInDto);

        entity.setRol(Roles.PLAYER);
        entity.setActive(false);

        Account registeredAccount = accountService.create(entity);

        AppUser registeredUser = new AppUser();

        registeredUser.setAccount(registeredAccount);

        return ResponseEntity.ok(
                appUserMapper.toDto(appUserService.create(registeredUser))
        );

    }

}
