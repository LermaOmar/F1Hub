package ptzt.f1Hub.instraestructure.repository.original;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ptzt.f1Hub.domain.models.original.market.Market;
import ptzt.f1Hub.domain.models.original.market.MarketItem;

import java.util.List;

@Repository
public interface MarketItemRepository extends JpaRepository<MarketItem, Long> {

    List<MarketItem> findAllByMarkets(List<Market> markets);

}