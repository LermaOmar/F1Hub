package ptzt.f1Hub.instraestructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ptzt.f1Hub.domain.models.market.Market;
import ptzt.f1Hub.domain.models.market.MarketItem;

import java.util.List;

@Repository
public interface MarketItemRepository extends JpaRepository<MarketItem, Long> {

    List<MarketItem> findAllByMarkets(List<Market> markets);

}