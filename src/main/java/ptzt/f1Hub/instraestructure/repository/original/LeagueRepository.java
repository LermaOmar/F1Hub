package ptzt.f1Hub.instraestructure.repository.original;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ptzt.f1Hub.domain.models.original.League;

@Repository
public interface LeagueRepository extends JpaRepository<League, Long> {
}