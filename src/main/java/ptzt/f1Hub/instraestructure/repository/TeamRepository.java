package ptzt.f1Hub.instraestructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ptzt.f1Hub.domain.models.Team;

import java.util.Optional;

public interface TeamRepository extends JpaRepository<Team, Long> {

    Optional<Team> findByName(String name);

}