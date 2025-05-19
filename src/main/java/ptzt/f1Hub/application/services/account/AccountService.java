package ptzt.f1Hub.application.services.account;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ptzt.f1Hub.domain.models.Account;
import ptzt.f1Hub.instraestructure.dto.in.account.AccountLoginDto;


public interface AccountService {

    Account create(Account account);

    String login(AccountLoginDto accountLoginDto);

    Account update(Account account);

    void delete(Account account);

    Account getById(Long id);

    Account getByEmail(String email);

    Page<Account> getAll(Pageable pageable);

    Page<Account> getAllActive(Pageable pageable);
}
