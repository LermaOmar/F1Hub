package ptzt.f1Hub.instraestructure.repository.original;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ptzt.f1Hub.domain.models.original.League;
import ptzt.f1Hub.domain.models.original.market.Market;

import java.util.Optional;

@Repository
public interface MarketRepository extends JpaRepository<Market, Long> {

    Optional<Market>  findByLeague(League league);
}