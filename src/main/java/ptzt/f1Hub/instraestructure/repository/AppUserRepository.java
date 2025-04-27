package ptzt.f1Hub.instraestructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ptzt.f1Hub.domain.models.AppUser;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {}