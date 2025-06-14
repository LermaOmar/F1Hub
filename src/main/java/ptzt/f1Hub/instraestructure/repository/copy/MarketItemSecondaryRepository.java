package ptzt.f1Hub.instraestructure.repository.copy;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ptzt.f1Hub.domain.models.copy.market.Market;
import ptzt.f1Hub.domain.models.copy.market.MarketItem;

import java.util.List;

@Repository
public interface MarketItemSecondaryRepository extends JpaRepository<MarketItem, Long> {

    List<MarketItem> findAllByMarkets(List<Market> markets);

}