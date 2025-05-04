package ptzt.f1Hub.application.services.market;

import ptzt.f1Hub.domain.models.market.Market;

import java.util.Optional;

public interface MarketService {

    Market create(Market market);

    Market update(Market market);

    Optional<Market> getById(Long id);

}
