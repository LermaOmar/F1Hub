package ptzt.f1Hub.instraestructure.repository.copy;

import org.springframework.data.jpa.repository.JpaRepository;
import ptzt.f1Hub.domain.models.copy.Account;
import ptzt.f1Hub.domain.models.copy.AppUser;

import java.util.Optional;

public interface AppUserSecondaryRepository extends JpaRepository<AppUser, Long> {

    Optional<AppUser> findByAccount(Account account);

}