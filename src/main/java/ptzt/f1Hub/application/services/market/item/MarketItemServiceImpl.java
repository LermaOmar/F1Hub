package ptzt.f1Hub.application.services.market.item;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import ptzt.f1Hub.application.services.market.MarketService;
import ptzt.f1Hub.domain.exceptions.EntityNotFoundException;
import ptzt.f1Hub.domain.models.market.Market;
import ptzt.f1Hub.domain.models.market.MarketItem;
import ptzt.f1Hub.instraestructure.repository.MarketItemRepository;

import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor_ = @Lazy)
public class MarketItemServiceImpl implements MarketItemService{

    private final MarketItemRepository marketItemRepository;
    private final MarketService marketService;

    @Override
    public void create(MarketItem marketItem) {

        marketItemRepository.save(marketItem);

    }

    @Override
    public void update(MarketItem marketItem) {

        marketItemRepository.save(marketItem);

    }

    @Override
    public void displayInMarket(MarketItem marketItem, List<Market> markets) {

        marketItem.setAvailable(true);
        markets.forEach(market -> marketItem.getMarkets().add(market));

        marketItemRepository.save(marketItem);

    }

    @Override
    public void hideInMarket(MarketItem marketItem, List<Market> markets) {

        if (!(markets.size() < marketService.getAll().size()))
            marketItem.setAvailable(false);

        List<Long> marketsIds = markets.stream().map(Market::getId).toList();

        marketItem.getMarkets().removeIf(market -> marketsIds.contains(market.getId()));
        marketItemRepository.save(marketItem);

    }

    @Override
    public MarketItem getById(Long id) {

        return marketItemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("There is no Market Item with that ID"));


    }

    @Override
    public List<MarketItem> getAllByMarkets(List<Market> markets) {

        return marketItemRepository.findAllByMarkets(markets);

    }

    @Override
    public List<MarketItem> getAll() {

        return marketItemRepository.findAll();

    }
}
