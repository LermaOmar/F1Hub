package ptzt.f1Hub.instraestructure.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import ptzt.f1Hub.application.services.account.AccountService;
import ptzt.f1Hub.domain.mappers.AccountMapper;
import ptzt.f1Hub.domain.models.original.Account;
import ptzt.f1Hub.instraestructure.dto.in.account.AccountInFullDto;
import ptzt.f1Hub.instraestructure.dto.out.account.AccountOutDto;
import ptzt.f1Hub.instraestructure.dto.out.shared.PageOutDto;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final AccountMapper accountMapper;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    public ResponseEntity<PageOutDto<AccountOutDto>> getAll(@PageableDefault Pageable pageable,
                                                            @RequestParam(required = false) boolean skipNotActive){

        Page<Account> page = skipNotActive ? accountService.getAllActive(pageable) : accountService.getAll(pageable);

        return ResponseEntity.ok(
                new PageOutDto<>(page.map(accountMapper::toDto))
        );

    }

    @PutMapping("/{id}")
    public ResponseEntity<AccountOutDto> update(@Valid @RequestBody AccountInFullDto accountInDto, @PathVariable Long id){

        Account entity = accountService.getById(id);

        accountMapper.toUpdate(accountInDto, entity);

        if (accountInDto.getPassword() != null && !(accountInDto.getPassword().isBlank()))
            entity.setPassword(passwordEncoder.encode(accountInDto.getPassword()));


        return ResponseEntity.ok(
                accountMapper.toDto(accountService.update(entity))
        );

    }

}
