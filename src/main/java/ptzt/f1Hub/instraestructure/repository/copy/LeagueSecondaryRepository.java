package ptzt.f1Hub.instraestructure.repository.copy;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ptzt.f1Hub.domain.models.copy.League;

@Repository
public interface LeagueSecondaryRepository extends JpaRepository<League, Long> {
}