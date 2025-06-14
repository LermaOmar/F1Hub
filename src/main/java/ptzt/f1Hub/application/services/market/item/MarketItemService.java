package ptzt.f1Hub.application.services.market.item;

import com.fasterxml.jackson.dataformat.yaml.snakeyaml.error.Mark;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ptzt.f1Hub.domain.models.original.AuctionableEntity;
import ptzt.f1Hub.domain.models.original.market.Market;
import ptzt.f1Hub.domain.models.original.market.MarketItem;

import java.util.List;

public interface MarketItemService {

    void create(MarketItem marketItem);

    void update(MarketItem marketItem);

    void displayInMarket(MarketItem marketItem, List<Market> market);

    void hideInMarket(MarketItem marketItem, List<Market> market);

    void updateMarketItems();

    MarketItem getById(Long id);

    MarketItem getByAuctionableEntity(AuctionableEntity auctionableEntity);

    List<MarketItem> getAll();

    Page<MarketItem> getAllByMarket(Market market, Pageable pageable);

    List<MarketItem> getAllAvailableByMarket(Market market);
    
    List<MarketItem> getAllByMarket(Market market);

    Page<MarketItem> getAll(Pageable pageable);


}
