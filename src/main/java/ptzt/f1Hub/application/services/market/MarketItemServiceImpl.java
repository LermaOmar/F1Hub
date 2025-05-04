package ptzt.f1Hub.application.services.market;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ptzt.f1Hub.domain.exceptions.EntityNotFoundException;
import ptzt.f1Hub.domain.models.market.MarketItem;
import ptzt.f1Hub.instraestructure.repository.MarketItemRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MarketItemServiceImpl implements MarketItemService{

    private final MarketItemRepository marketItemRepository;

    @Override
    public void create(MarketItem marketItem) {

        marketItemRepository.save(marketItem);

    }



    @Override
    public void displayInMarket(MarketItem marketItem) {

        marketItem.setAvailable(true);

        marketItemRepository.save(marketItem);

    }

    @Override
    public void hideInMarket(MarketItem marketItem) {

        marketItem.setAvailable(false);
        marketItem.setMarket(null);
        marketItemRepository.save(marketItem);

    }

    @Override
    public MarketItem getById(Long id) {

        return marketItemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("There is no Market Item with that ID"));


    }

    @Override
    public List<MarketItem> getAll() {

        return marketItemRepository.findAll();

    }
}
