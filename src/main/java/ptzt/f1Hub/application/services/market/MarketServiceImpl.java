package ptzt.f1Hub.application.services.market;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ptzt.f1Hub.application.services.league.LeagueService;
import ptzt.f1Hub.application.services.offer.OfferService;
import ptzt.f1Hub.exceptions.EntityNotFoundException;
import ptzt.f1Hub.domain.models.*;
import ptzt.f1Hub.domain.models.market.Market;
import ptzt.f1Hub.domain.models.market.MarketItem;
import ptzt.f1Hub.domain.models.market.Offer;
import ptzt.f1Hub.instraestructure.repository.MarketRepository;

import java.util.*;

@Service
@RequiredArgsConstructor
public class MarketServiceImpl implements MarketService{

    private final MarketRepository marketRepository;
    private final LeagueService leagueService;
    private final OfferService offerService;


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
    public Market getById(Long id) {

        return marketRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("There is no market with that ID"));

    }

    @Override
    public Market getByLeague(League league) {

        return marketRepository.findByLeague(league)
                .orElseThrow(() -> new EntityNotFoundException("There is no market with that ID"));

    }

    @Override
    public List<Market> getAll() {

        return marketRepository.findAll();

    }

    @Transactional
    @Override
    public Map<League, Map<MarketItem, Offer>> finalizeAuction() {

        // Retrieve all offers and all leagues
        List<Offer> allOffers = offerService.getAll();
        List<League> allLeagues = leagueService.getAll();

        // This map will hold the winning offers per league and per market item
        Map<League, Map<MarketItem, Offer>> winningOffersByLeague = new HashMap<>();

        // Iterate over all leagues
        allLeagues.forEach(league -> {
            // Create a map to hold the highest offer for each market item within this league
            Map<MarketItem, Offer> highestOffersForLeague = new HashMap<>();

            // Filter and process offers for the current league
            allOffers.stream()
                    .filter(offer -> offer.getLeague().equals(league)) // Filter offers by league
                    .forEach(offer -> {
                        MarketItem marketItem = offer.getMarketItem();

                        // Update the highest offer for each market item, comparing by the offer amount
                        highestOffersForLeague.merge(marketItem, offer,
                                (existingOffer, newOffer) -> newOffer.getAmount() > existingOffer.getAmount() ? newOffer : existingOffer);
                    });

            // Add the highest offers for the current league to the final result
            winningOffersByLeague.put(league, highestOffersForLeague);
        });

        // Return the map with winning offers by league and market item
        return winningOffersByLeague;
    }



}
