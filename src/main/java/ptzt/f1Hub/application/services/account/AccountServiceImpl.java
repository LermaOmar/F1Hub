package ptzt.f1Hub.application.services.account;

import lombok.RequiredArgsConstructor;
import org.hibernate.NonUniqueResultException;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import ptzt.f1Hub.application.services.mail.MailService;
import ptzt.f1Hub.application.services.verificationToken.VerificationTokenService;
import ptzt.f1Hub.config.security.AppUserDetailService;
import ptzt.f1Hub.config.security.JwtService;
import ptzt.f1Hub.exceptions.*;
import ptzt.f1Hub.domain.models.original.Account;
import ptzt.f1Hub.instraestructure.dto.in.account.AccountLoginDto;
import ptzt.f1Hub.instraestructure.repository.original.AccountRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor(onConstructor_ = @Lazy)
public class AccountServiceImpl implements AccountService{

    private final AccountRepository accountRepository;

    private final JwtService jwtService;
    private final AppUserDetailService appUserDetailService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final VerificationTokenService verificationTokenService;
    private final MailService mailService;


    @Override
    public Account create(Account account) {

        try {
            if (accountRepository.findByUsernameOrEmail(account.getUsername(),account.getEmail()).isPresent())
                throw new UnproccesableEntityException("Email or Username already assigned to other account");

            account.setPassword(passwordEncoder.encode(account.getPassword()));

            Account entity = accountRepository.save(account);

            mailService.sendVerificationEmail(entity, verificationTokenService.creteToken(entity).getToken());
            return entity;

        } catch (NonUniqueResultException e) {
            throw new UnproccesableEntityException("Email or Username already assigned to other account");
        }

    }

    @Override
    public String login(AccountLoginDto accountLoginDto) {
        try{

            if (!accountRepository.findByEmail(accountLoginDto.getEmail()).get().isActive())
                throw new AccountNotActiveException("Active the account before login");

            if (accountLoginDto.getEmail().equals("efeuno.hub@gmail.com"))
                    throw new BadRequestException("You can not login with the system account");

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

        if (account.getEmail().equals("efeuno.hub@gmail.com")){
            throw new BadRequestException("You can not modify the system account");
        }

        try{

            Optional<Account> opAccount = accountRepository.findByUsernameOrEmail(account.getUsername(),account.getEmail());

            if (opAccount.isPresent() && !opAccount.get().getId().equals(account.getId()))
                throw new UnproccesableEntityException("Email or Username already assigned to other account");

            return accountRepository.save(account);

        } catch (NonUniqueResultException e) {
            throw new UnproccesableEntityException("Email or Username already assigned to other account");
        }

    }

    @Override
    public void delete(Account account) {

        accountRepository.delete(account);

    }

    @Override
    public void deactivate(Account account) {

        if (account.getEmail().equals("efeuno.hub@gmail.com")){
            throw new BadRequestException("You can not deactivate the system account");
        }
        account.setActive(false);
        update(account);

    }

    @Override
    public void verify(String token) {

        Account account = verificationTokenService.verifyToken(token);
        account.setActive(true);
        update(account);

    }

    @Override
    public void resendVerification(String email) {

        Account account = getByEmail(email);

        if (account.isActive())
            throw new BadRequestException("Account already verified");

        verificationTokenService.invalidateAllTokensForUser(account);

        mailService.sendVerificationEmail(account, verificationTokenService.creteToken(account).getToken());


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

    @Override
    public Page<Account> getAllActive(Pageable pageable) {

        return accountRepository.findAllByActiveTrue(pageable);

    }
}
