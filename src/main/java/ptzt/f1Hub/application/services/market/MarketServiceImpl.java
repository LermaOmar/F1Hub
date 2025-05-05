package ptzt.f1Hub.application.services.market;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ptzt.f1Hub.application.services.league.LeagueService;
import ptzt.f1Hub.application.services.market.item.MarketItemService;
import ptzt.f1Hub.application.services.offer.OfferService;
import ptzt.f1Hub.domain.exceptions.OffersNotAvailableException;
import ptzt.f1Hub.domain.models.*;
import ptzt.f1Hub.domain.models.market.Market;
import ptzt.f1Hub.domain.models.market.MarketItem;
import ptzt.f1Hub.domain.models.market.Offer;
import ptzt.f1Hub.instraestructure.repository.MarketRepository;

import java.util.*;
import java.util.stream.Collectors;

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
    public Optional<Market> getById(Long id) {

        return marketRepository.findById(id);

    }

    @Transactional
    @Override
    public Map<League, Map<MarketItem, Offer>> finalizeAuction() {

        List<Offer> allOffers = offerService.getAll();
        List<League> allLeagues = leagueService.getAll();

        Map<League, Map<MarketItem, Offer>> winningOffersByLeague = new HashMap<>();

        // Process each league independently
        allLeagues.forEach(league -> {
            Map<MarketItem, Offer> highestOffersForLeague = new HashMap<>();

            // Filter and process offers for the current league
            allOffers.stream()
                    .filter(offer ->
                            isUserInLeague(offer.getAppUser(), league))  // Only consider offers from users in the current league
                    .forEach(offer -> {
                        MarketItem marketItem = offer.getMarketItem();

                        // Compare offers for the same MarketItem within this league and keep the highest offer
                        highestOffersForLeague.merge(marketItem, offer,
                                (existingOffer, newOffer) -> newOffer.getAmount() > existingOffer.getAmount() ? newOffer : existingOffer);
                    });

            // Add the highest offers for the league to the final result
            winningOffersByLeague.put(league, highestOffersForLeague);
        });

        return winningOffersByLeague;
    }


    private boolean isUserInLeague(AppUser appUser, League league) {
        return appUser.getLineUps().stream()
                .anyMatch(lineUp -> lineUp.getLeague().equals(league));  // Verify if user is part of the league
    }

}
