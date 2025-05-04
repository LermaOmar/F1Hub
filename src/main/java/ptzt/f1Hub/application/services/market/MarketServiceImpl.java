package ptzt.f1Hub.application.services.market;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ptzt.f1Hub.application.services.market.item.MarketItemService;
import ptzt.f1Hub.domain.exceptions.OffersNotAvailableException;
import ptzt.f1Hub.domain.models.AuctionableEntity;
import ptzt.f1Hub.domain.models.Driver;
import ptzt.f1Hub.domain.models.LineUp;
import ptzt.f1Hub.domain.models.Team;
import ptzt.f1Hub.domain.models.market.Market;
import ptzt.f1Hub.domain.models.market.MarketItem;
import ptzt.f1Hub.domain.models.market.Offer;
import ptzt.f1Hub.instraestructure.repository.MarketRepository;

import java.util.Comparator;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MarketServiceImpl implements MarketService{

    private final MarketRepository marketRepository;
    private final MarketItemService marketItemService;

    @Transactional
    @Override
    public Market create(Market market) {

        return marketRepository.save(market);

    }

    @Transactional
    @Override
    public Market update(Market market) {

        return marketRepository.save(market);

    }

    @Override
    public Optional<Market> getById(Long id) {

        return marketRepository.findById(id);

    }

    @Transactional
    @Override
    //Todo End the method and refactor the signature of it
    public void finalizeAuction(Long id) {

        /*MarketItem item = marketItemService.getById(id);

        Offer winningOffer = item.getOffers().stream()
                .max(Comparator.comparing(Offer::getAmount))
                .orElseThrow(() -> new OffersNotAvailableException("No offers for this item"));

        if (item.getAuctionableEntity() instanceof Driver driver) {

            Driver entity = (Driver) item.getAuctionableEntity();
            LineUp lineUp = new LineUp();
            entity.setLineUps(entity.getLineUps()));

        } else{
            Team entity = (Team) item.getAuctionableEntity();

        }


        // Desvincular el MarketItem del Market
        item.setAvailable(false);
        item.setMarket(null);

        auctionableEntityRepository.save(entity);
        marketItemRepository.save(item);*/

    }

    private Optional<Offer> getBestOffer(MarketItem marketItem) {
        return marketItem.getOffers().stream()
                .max(Comparator.comparing(Offer::getAmount));
    }
}
