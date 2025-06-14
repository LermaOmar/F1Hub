package ptzt.f1Hub.application.services.league;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ptzt.f1Hub.application.services.appUser.AppUserService;
import ptzt.f1Hub.application.services.budget.BudgetService;
import ptzt.f1Hub.application.services.lineUp.LineUpService;
import ptzt.f1Hub.application.services.market.MarketService;
import ptzt.f1Hub.application.services.market.item.MarketItemService;
import ptzt.f1Hub.domain.models.original.AppUser;
import ptzt.f1Hub.exceptions.EntityNotFoundException;
import ptzt.f1Hub.domain.models.original.League;
import ptzt.f1Hub.domain.models.original.market.Market;
import ptzt.f1Hub.domain.models.original.market.MarketItem;
import ptzt.f1Hub.exceptions.UnproccesableEntityException;
import ptzt.f1Hub.instraestructure.repository.original.LeagueRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor(onConstructor_ = @Lazy)
public class LeagueServiceImpl implements LeagueService{

    private final LeagueRepository leagueRepository;
    private final AppUserService appUserService;
    private final LineUpService lineUpService;
    private final MarketService marketService;
    private final BudgetService budgetService;
    private final MarketItemService marketItemService;

    @Transactional
    @Override
    public League create(League league, AppUser appUser) {

        if (leagueRepository.findByName(league.getName()).isPresent())
            throw new UnproccesableEntityException("League name already assigned");


        Market market = new Market();
        market.setLeague(league);

        Market entityMarket = marketService.create(market);

        league.setMarket(entityMarket);


        League entity = leagueRepository.save(league);

        List<MarketItem> items = marketItemService.getAll();

        items.forEach(marketItem -> {
            marketItem.getMarkets().add(entityMarket);
            marketItemService.update(marketItem);
        });

        marketItemService.updateMarketItems();

        appUserService.joinLeague(appUser,entity.getId());

        return entity;
    }

    @Override
    public League update(League league) {

        Optional<League> opLeague = leagueRepository.findByName(league.getName());

        if (opLeague.isPresent() && !opLeague.get().getId().equals(league.getId()))
            throw new UnproccesableEntityException("League name already assigned");

        return leagueRepository.save(league);

    }

    @Transactional
    @Override
    public void delete(League league) {


        league.getLineUps().forEach(lineUpService::delete);
        league.getBudgets().forEach(budgetService::delete);
        marketService.delete(league.getMarket());

        leagueRepository.delete(league);

    }

    @Override
    public League getById(Long id) {

        return leagueRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("There is no league with that ID"));

    }

    @Override
    public Page<League> getAllByUser(Pageable pageable, AppUser appUser) {

        return leagueRepository.findDistinctLeaguesByAppUserId(pageable,appUser.getId());

    }

    @Override
    public List<League> getAll() {

        return leagueRepository.findAll();

    }

}
