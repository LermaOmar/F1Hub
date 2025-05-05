package ptzt.f1Hub.application.services.market;

import ptzt.f1Hub.domain.models.League;
import ptzt.f1Hub.domain.models.market.Market;
import ptzt.f1Hub.domain.models.market.MarketItem;
import ptzt.f1Hub.domain.models.market.Offer;

import java.util.Map;
import java.util.Optional;

public interface MarketService {

    Market create(Market market);

    Market update(Market market);

    Optional<Market> getById(Long id);

    Map<League, Map<MarketItem, Offer>>  finalizeAuction();

}
