package ptzt.f1Hub.application.services.market.item;

import ptzt.f1Hub.domain.models.market.MarketItem;

import java.util.List;

public interface MarketItemService {

    void create(MarketItem marketItem);

    void displayInMarket(MarketItem marketItem);

    void hideInMarket(MarketItem marketItem);

    MarketItem getById(Long id);

    List<MarketItem> getAll();

}
