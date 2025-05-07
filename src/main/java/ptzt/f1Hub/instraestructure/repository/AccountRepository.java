package ptzt.f1Hub.instraestructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ptzt.f1Hub.domain.models.Account;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByUsernameOrEmail(String username, String email);

    Optional<Account> findByEmail(String email);

    Optional<Account> findByUsername(String username);
}