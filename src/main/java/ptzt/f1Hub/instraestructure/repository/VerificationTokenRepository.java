package ptzt.f1Hub.instraestructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ptzt.f1Hub.domain.models.VerificationToken;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

    Optional<VerificationToken> findByToken(String token);

    List<VerificationToken> findByAccountId(Long id);

    long deleteByUsedFalseAndExpiresAtBefore(LocalDateTime dateTime);

}