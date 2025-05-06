package ptzt.f1Hub.application.services.market;

import ptzt.f1Hub.domain.models.League;
import ptzt.f1Hub.domain.models.market.Market;
import ptzt.f1Hub.domain.models.market.MarketItem;
import ptzt.f1Hub.domain.models.market.Offer;

import java.util.List;
import java.util.Map;

public interface MarketService {

    Market create(Market market);

    Market update(Market market);

    Market getById(Long id);

    Market getByLeague(League league);

    List<Market> getAll();

    Map<League, Map<MarketItem, Offer>>  finalizeAuction();

}
