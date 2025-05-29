package ptzt.f1Hub.instraestructure.repository.copy;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ptzt.f1Hub.domain.models.copy.Account;

import java.util.Optional;

public interface AccountSecondaryRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByUsernameOrEmail(String username, String email);

    Optional<Account> findByEmail(String email);

    Optional<Account> findByUsername(String username);

    Page<Account >findAllByActiveTrue(Pageable pageable);

}