package ptzt.f1Hub.application.services.account;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import ptzt.f1Hub.config.security.AppUserDetailService;
import ptzt.f1Hub.config.security.JwtService;
import ptzt.f1Hub.exceptions.AccountNotActiveException;
import ptzt.f1Hub.exceptions.AuthenticationException;
import ptzt.f1Hub.exceptions.EntityNotFoundException;
import ptzt.f1Hub.exceptions.UnproccesableEntityException;
import ptzt.f1Hub.domain.models.Account;
import ptzt.f1Hub.instraestructure.dto.in.account.AccountLoginDto;
import ptzt.f1Hub.instraestructure.repository.AccountRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService{

    private final AccountRepository accountRepository;

    private final JwtService jwtService;
    private final AppUserDetailService appUserDetailService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;


    @Override
    public Account create(Account account) {

        if (accountRepository.findByUsernameOrEmail(account.getUsername(),account.getEmail()).isPresent())
            throw new UnproccesableEntityException("Email or Username already assigned to other account");;

        account.setPassword(passwordEncoder.encode(account.getPassword()));
        return accountRepository.save(account);

    }

    @Override
    public String login(AccountLoginDto accountLoginDto) {
        try{
            if (!accountRepository.findByEmail(accountLoginDto.getEmail()).get().isActive())
                throw new AccountNotActiveException("Active the account before login");


            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(accountLoginDto.getEmail(),accountLoginDto.getPassword()));

            if (authentication.isAuthenticated())
                return jwtService.generateToken(appUserDetailService.loadUserByUsername(accountLoginDto.getEmail()));

            else
                throw new AuthenticationException("Invalid credentials");
        } catch (Exception e) {
            if (!e.getMessage().startsWith("Active"))
                throw new AuthenticationException("Invalid credentials");

            throw new AccountNotActiveException("Active the account before login");
        }
    }

    @Override
    public Account update(Account account) {

        Optional<Account> opAccount = accountRepository.findByUsernameOrEmail(account.getUsername(),account.getEmail());
        if (opAccount.isPresent() && !opAccount.get().getId().equals(account.getId()))
            throw new UnproccesableEntityException("Email or Username already assigned to other account");

        return accountRepository.save(account);

    }

    @Override
    public void delete(Account account) {

        accountRepository.delete(account);

    }

    @Override
    public Account getById(Long id) {

        return accountRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("There is no account with that ID"));
    }

    @Override
    public Account getByEmail(String email) {

        return accountRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("There is no account with that email"));
    }

    @Override
    public Page<Account> getAll(Pageable pageable) {

        return accountRepository.findAll(pageable);

    }
}
