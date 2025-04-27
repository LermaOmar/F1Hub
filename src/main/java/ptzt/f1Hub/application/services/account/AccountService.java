package ptzt.f1Hub.application.services.account;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ptzt.f1Hub.domain.models.Account;


public interface AccountService {

    Account create(Account account);

    Account update(Account account);

    void delete(Account account);

    Account getById(Long id);

    Page<Account> getAll(Pageable pageable);
}
