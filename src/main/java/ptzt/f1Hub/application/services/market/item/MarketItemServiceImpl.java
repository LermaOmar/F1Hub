package ptzt.f1Hub.application.services.market.item;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ptzt.f1Hub.application.services.account.AccountService;
import ptzt.f1Hub.application.services.appUser.AppUserService;
import ptzt.f1Hub.application.services.lineUp.LineUpService;
import ptzt.f1Hub.application.services.market.MarketService;
import ptzt.f1Hub.application.services.offer.OfferService;
import ptzt.f1Hub.domain.models.original.*;
import ptzt.f1Hub.domain.models.original.market.Offer;
import ptzt.f1Hub.exceptions.EntityNotFoundException;
import ptzt.f1Hub.domain.models.original.market.Market;
import ptzt.f1Hub.domain.models.original.market.MarketItem;
import ptzt.f1Hub.instraestructure.repository.original.MarketItemRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor(onConstructor_ = @Lazy)
public class MarketItemServiceImpl implements MarketItemService{

    private final MarketItemRepository marketItemRepository;
    private final MarketService marketService;
    private final OfferService offerService;
    private final LineUpService lineUpService;
    private final AppUserService appUserService;
    private final AccountService accountService;

    private final static int ITEMS_PER_MARKET = 7;
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

        for (Market market : markets) {
            boolean alreadyPresent = marketItem.getMarkets().stream()
                    .anyMatch(m -> m.getId().equals(market.getId()));
            if (!alreadyPresent) {

                marketItem.setAvailable(true);
                marketItem.getMarkets().add(market);

                //LineUps of league
                List<LineUp> leagueLineUps = lineUpService.getAllByLeague(Pageable.unpaged(),market.getLeague()).getContent();

                //Ids of the Auctionable Entities of the leagues
                Set<Long> lockedEntityIds = leagueLineUps.stream()
                        .flatMap(lu -> {
                            Stream<Long> teamId = lu.getTeam() != null
                                    ? Stream.of(lu.getTeam().getId())
                                    : Stream.empty();
                            Stream<Long> driverIds = lu.getDrivers().stream()
                                    .map(AuctionableEntity::getId);
                            return Stream.concat(teamId, driverIds);
                        })
                        .collect(Collectors.toSet());

                if (lockedEntityIds.contains(marketItem.getAuctionableEntity().getId())){
                    Offer sysOffer = Offer.builder()
                            .appUser(appUserService.getByAccount(accountService.getByEmail("efeuno.hub@gmail.com")))
                            .amount(Math.round(1.1 * marketItem.getAuctionableEntity().getPrice()))
                            .createdAt(LocalDateTime.now())
                            .marketItem(marketItem)
                            .league(market.getLeague())
                            .build();

                    Offer saved = offerService.create(sysOffer);
                    marketItem.getOffers().add(saved);
                }
            }
        }
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
    public void updateMarketItems() {
        List<Market> markets   = marketService.getAll();
        List<MarketItem> allItems = getAll().stream().filter(marketItem -> marketItem.getAuctionableEntity().getActive()).toList();
        if (markets.isEmpty() || allItems.isEmpty()) return;

        for (Market market : markets) {
            League league = market.getLeague();

            //Items part of any lineup
            Set<MarketItem> lockedItems = allItems.stream()
                    .filter(mi -> isInLineUp(mi, league))
                    .collect(Collectors.toSet());

            //Hide items in market that are not part of any lineup
            List<MarketItem> currentlyVisible = allItems.stream()
                    .filter(mi -> mi.getMarkets().contains(market))
                    .filter(mi -> !lockedItems.contains(mi))
                    .toList();
            currentlyVisible.forEach(mi ->
                    hideInMarket(mi, List.of(market))
            );

            //Select the available items
            List<MarketItem> candidates = allItems.stream()
                    .filter(mi -> !mi.getMarkets().contains(market))
                    .filter(mi -> !lockedItems.contains(mi))
                    .collect(Collectors.toList());

            //Shuffle to get random item into market
            Collections.shuffle(candidates);
            Set<MarketItem> toDisplay = new HashSet<>(
                    candidates.subList(0, Math.min(ITEMS_PER_MARKET, candidates.size()))
            );
            toDisplay.forEach(mi ->
                    displayInMarket(mi, List.of(market))
            );
        }
    }


    private boolean isInLineUp(MarketItem mi, League league) {
        var entity = mi.getAuctionableEntity();
        if (entity instanceof Driver driver) {
            return driver.getLineUps().stream()
                    .anyMatch(lu -> lu.getLeague().getId().equals(league.getId()));
        } else if (entity instanceof Team team) {
            return team.getLineUps().stream()
                    .anyMatch(lu -> lu.getLeague().getId().equals(league.getId()));
        }
        return false;
    }

    @Override
    public MarketItem getById(Long id) {

        return marketItemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("There is no Market Item with that ID"));


    }

    @Override
    public MarketItem getByAuctionableEntity(AuctionableEntity auctionableEntity) {

        return marketItemRepository.findByAuctionableEntity(auctionableEntity)
                .orElseThrow(() -> new EntityNotFoundException("There is no Market Item with that ID"));

    }

    @Override
    public List<MarketItem> getAll() {

        return marketItemRepository.findAll();

    }

    @Override
    public Page<MarketItem> getAllByMarket(Market market, Pageable pageable) {

        return marketItemRepository.findAllByMarketsContainingAndAvailableTrue(market,pageable);

    }

    @Override
    public List<MarketItem> getAllAvailableByMarket(Market market) {

        return marketItemRepository.findAllByMarketsContainingAndAvailableTrue(market,Pageable.unpaged()).getContent();

    }

    @Override
    public List<MarketItem> getAllByMarket(Market market) {

        return marketItemRepository.findAllByMarketsContaining(market);

    }


    @Override
    public Page<MarketItem> getAll(Pageable pageable) {

        return marketItemRepository.findAll(pageable);

    }
}
