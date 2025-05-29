package ptzt.f1Hub.instraestructure.repository.copy;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ptzt.f1Hub.domain.models.copy.League;
import ptzt.f1Hub.domain.models.copy.market.Market;

import java.util.Optional;

@Repository
public interface MarketSecondaryRepository extends JpaRepository<Market, Long> {

    Optional<Market>  findByLeague(League league);
}