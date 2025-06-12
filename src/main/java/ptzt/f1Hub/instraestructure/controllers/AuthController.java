package ptzt.f1Hub.instraestructure.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import ptzt.f1Hub.application.services.account.AccountService;
import ptzt.f1Hub.application.services.appUser.AppUserService;
import ptzt.f1Hub.domain.mappers.AccountMapper;
import ptzt.f1Hub.domain.mappers.AppUserMapper;
import ptzt.f1Hub.domain.models.original.Account;
import ptzt.f1Hub.domain.models.original.AppUser;
import ptzt.f1Hub.instraestructure.dto.in.account.AccountInDto;
import ptzt.f1Hub.instraestructure.dto.in.account.AccountLoginDto;
import ptzt.f1Hub.instraestructure.dto.out.appUser.AppUserOutDto;
import ptzt.f1Hub.instraestructure.dto.out.shared.DefaultResponseDto;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AccountService accountService;
    private final AppUserService appUserService;

    private final AccountMapper accountMapper;
    private final AppUserMapper appUserMapper;

    private final PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<AppUserOutDto> register(@Valid @RequestBody AccountInDto accountInDto){

        Account entity = accountMapper.toEntity(accountInDto);

        Account registeredAccount = accountService.create(entity);

        AppUser registeredUser = new AppUser();

        registeredUser.setAccount(registeredAccount);

        return ResponseEntity.ok(
                appUserMapper.toDto(appUserService.create(registeredUser))
        );

    }

    @PostMapping("/login")
    public ResponseEntity<DefaultResponseDto> login(@Valid @RequestBody AccountLoginDto accountLoginDto){

        return ResponseEntity.ok(
                new DefaultResponseDto(200, accountService.login(accountLoginDto ))
        );
    }

    @PostMapping("/check")
    public ResponseEntity<DefaultResponseDto> checkToken(){

        return ResponseEntity.ok(
                new DefaultResponseDto(200,"Valid token")
        );

    }

    @PutMapping("/activate")
    public ResponseEntity<DefaultResponseDto> verify(@RequestParam String token) {

        accountService.verify(token);

        return ResponseEntity.ok(new DefaultResponseDto(
                200, "Account activated successfully"));
    }

    @PutMapping("/resend-verification")
    public ResponseEntity<DefaultResponseDto> resendVerification(@RequestParam String email) {
        accountService.resendVerification(email);

        return ResponseEntity.ok(new DefaultResponseDto(
                200, "Verification resent successfully"
        ));
    }

}
