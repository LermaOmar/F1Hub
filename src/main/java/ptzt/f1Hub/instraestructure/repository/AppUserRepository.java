package ptzt.f1Hub.instraestructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ptzt.f1Hub.domain.models.Account;
import ptzt.f1Hub.domain.models.AppUser;

import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    Optional<AppUser> findByAccount(Account account);

}