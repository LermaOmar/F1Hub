package ptzt.f1Hub.application.services.appUser;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import ptzt.f1Hub.domain.models.Account;
import ptzt.f1Hub.domain.models.AppUser;
import ptzt.f1Hub.domain.models.League;


public interface AppUserService {

    AppUser create(AppUser appUser);

    AppUser update(AppUser appUser);

    void delete(Long id);

    AppUser getById(Long id);

    AppUser getByAccount(Account account);

    Page<AppUser> getAll(Pageable pageable);

    void joinLeague(AppUser appUser, Long league);

    void leaveLeague(AppUser appUser, Long league);
}
