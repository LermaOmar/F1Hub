package ptzt.f1Hub.application.services.league;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ptzt.f1Hub.application.services.lineUp.LineUpService;
import ptzt.f1Hub.application.services.market.MarketService;
import ptzt.f1Hub.application.services.market.item.MarketItemService;
import ptzt.f1Hub.domain.exceptions.EntityNotFoundException;
import ptzt.f1Hub.domain.models.League;
import ptzt.f1Hub.domain.models.LineUp;
import ptzt.f1Hub.domain.models.market.Market;
import ptzt.f1Hub.domain.models.market.MarketItem;
import ptzt.f1Hub.instraestructure.repository.LeagueRepository;

import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor_ = @Lazy)
public class LeagueServiceImpl implements LeagueService{

    private final LeagueRepository leagueRepository;
    private final LineUpService lineUpService;
    private final MarketService marketService;
    private final MarketItemService marketItemService;

    @Transactional
    @Override
    public League create(League league) {

        List<MarketItem> items = marketItemService.getAllByMarkets(marketService.getAll());

        Market market = new Market();
        market.setLeague(league);

        Market entityMarket = marketService.create(market);

        league.setMarket(entityMarket);


        League entity = leagueRepository.save(league);

        items.forEach(marketItem -> {
            marketItem.getMarkets().add(entityMarket);
            marketItemService.update(marketItem);
            if (marketItem.getAvailable())
                marketItemService.displayInMarket(marketItem, List.of(entityMarket));
        });

        return entity;
    }

    @Override
    public League update(League league) {

        return leagueRepository.save(league);

    }

    @Transactional
    @Override
    public void delete(League league) {

        league.getLineUps().forEach(lineUpService::delete);

        leagueRepository.delete(league);

    }

    @Override
    public League getById(Long id) {

        return leagueRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("There is no league with that ID"));

    }

    @Override
    public Page<League> getAll(Pageable pageable) {

        return leagueRepository.findAll(pageable);

    }

    @Override
    public List<League> getAll() {

        return leagueRepository.findAll();

    }

}
