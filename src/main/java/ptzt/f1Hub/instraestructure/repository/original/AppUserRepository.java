package ptzt.f1Hub.instraestructure.repository.original;

import org.springframework.data.jpa.repository.JpaRepository;
import ptzt.f1Hub.domain.models.original.Account;
import ptzt.f1Hub.domain.models.original.AppUser;

import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    Optional<AppUser> findByAccount(Account account);

}