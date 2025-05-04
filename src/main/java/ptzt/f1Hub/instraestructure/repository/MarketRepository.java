package ptzt.f1Hub.instraestructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ptzt.f1Hub.domain.models.market.Market;

@Repository
public interface MarketRepository extends JpaRepository<Market, Long> {
}