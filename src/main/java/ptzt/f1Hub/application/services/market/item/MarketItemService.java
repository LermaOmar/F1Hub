package ptzt.f1Hub.application.services.market.item;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ptzt.f1Hub.domain.models.market.Market;
import ptzt.f1Hub.domain.models.market.MarketItem;

import java.util.List;

public interface MarketItemService {

    void create(MarketItem marketItem);

    void update(MarketItem marketItem);

    void displayInMarket(MarketItem marketItem, List<Market> market);

    void hideInMarket(MarketItem marketItem, List<Market> market);

    MarketItem getById(Long id);

    List<MarketItem> getAllByMarkets(List<Market> markets);

    List<MarketItem> getAll();

    Page<MarketItem> getAll(Pageable pageable);


}
