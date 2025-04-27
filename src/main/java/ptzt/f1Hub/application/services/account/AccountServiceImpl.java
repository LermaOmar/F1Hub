package ptzt.f1Hub.application.services.account;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ptzt.f1Hub.application.services.appUser.AppUserService;
import ptzt.f1Hub.domain.exceptions.EntityNotFoundException;
import ptzt.f1Hub.domain.exceptions.UnproccesableEntityException;
import ptzt.f1Hub.domain.models.Account;
import ptzt.f1Hub.instraestructure.repository.AccountRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService{

    private final AccountRepository accountRepository;
    private final AppUserService appUserService;

    @Override
    public Account create(Account account) {

        if (accountRepository.findByUsernameOrEmail(account.getUsername(),account.getEmail()).isPresent())
            throw new UnproccesableEntityException("Las cuentas deben tener email y username únicos");

        return accountRepository.save(account);

    }

    @Override
    public Account update(Account account) {

        Optional<Account> opAccount = accountRepository.findByUsernameOrEmail(account.getUsername(),account.getEmail());
        if (opAccount.isPresent() && !opAccount.get().getId().equals(account.getId()))
            throw new UnproccesableEntityException("Las cuentas deben tener email y username únicos");

        return accountRepository.save(account);

    }

    @Override
    public void delete(Account account) {

        accountRepository.delete(account);

    }

    @Override
    public Account getById(Long id) {

        return accountRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No existe una cuenta con ese ID"));
    }

    @Override
    public Page<Account> getAll(Pageable pageable) {

        return accountRepository.findAll(pageable);

    }
}
